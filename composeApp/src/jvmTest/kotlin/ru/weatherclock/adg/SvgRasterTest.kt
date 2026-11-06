package ru.weatherclock.adg

import kotlinx.coroutines.runBlocking
import org.jetbrains.skia.Color
import org.jetbrains.skia.Data
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Surface
import org.jetbrains.skia.svg.SVGDOM
import org.junit.Test
import java.io.File

/**
 * Растеризация bundled SVG-иконок в PNG (build/icon-raster/) — визуальная
 * регрессионная проверка: файлы можно открыть и убедиться, что декодер
 * (skiko напрямую и через coil) выдаёт непустые пиксели.
 */
class SvgRasterTest {

    private fun locate(set: String, name: String): File =
        sequenceOf(
            File("src/commonMain/composeResources/files/weather_icons/$set/$name.svg"),
            File("composeApp/src/commonMain/composeResources/files/weather_icons/$set/$name.svg"),
        ).firstOrNull { it.exists() }
            ?: error("SVG not found: $set/$name")

    private fun outDir(): File = File("build/icon-raster").apply { mkdirs() }

    private fun savePng(image: org.jetbrains.skia.Image, out: File) {
        val data = image.encodeToData(EncodedImageFormat.PNG)
        out.writeBytes(data?.bytes ?: error("PNG encode failed"))
    }

    private fun opaqueCount(image: org.jetbrains.skia.Image): Int {
        val pixmap = image.peekPixels()
            ?: error("image has no raster pixels")
        var count = 0
        for (y in 0 until image.height step 2) {
            for (x in 0 until image.width step 2) {
                if ((pixmap.getColor(x, y) ushr 24) > 8) count++
            }
        }
        return count
    }

    private fun assertNotBlank(image: org.jetbrains.skia.Image, label: String) {
        val total = (image.width / 2) * (image.height / 2)
        val opaque = opaqueCount(image)
        require(opaque > total / 20) {
            "$label: пустой рендер ($opaque/$total непрозрачных сэмплов)"
        }
        println("$label: opaque=$opaque/$total")
    }

    private fun drawToSurface(draw: (org.jetbrains.skia.Canvas) -> Unit): org.jetbrains.skia.Image {
        val surface = Surface.makeRasterN32Premul(256, 256)
        surface.canvas.clear(Color.TRANSPARENT)
        draw(surface.canvas)
        return surface.makeImageSnapshot()
    }

    @Test
    fun `skiko renders bundled svg with visible pixels`() {
        val out = outDir()
        for (set in listOf("meteocons-fill", "meteocons", "twemoji", "wi")) {
            val bytes = locate(set, "owm-10d").readBytes()
            val dom = SVGDOM(Data.makeFromBytes(bytes))
            dom.setContainerSize(256f, 256f)
            val image = drawToSurface { dom.render(it) }
            savePng(image, File(out, "$set-direct.png"))
            assertNotBlank(image, "skiko/$set")
        }
    }

    @Test
    fun `coil decodes bundled bytes to visible image`() = runBlocking {
        val context = coil3.PlatformContext.INSTANCE
        val loader = coil3.ImageLoader.Builder(context).build()
        val bytes = locate("meteocons-fill", "owm-10d").readBytes()
        val request = coil3.request.ImageRequest.Builder(context)
            .data(bytes)
            .size(256)
            .build()
        val result = loader.execute(request) as coil3.request.SuccessResult
        val coilImage = result.image
        val image = drawToSurface { coilImage.draw(it) }
        savePng(image, File(outDir(), "coil-bytes.png"))
        assertNotBlank(image, "coil/bytes")
    }
}
