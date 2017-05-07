package org.asciidoctor.diagram

import org.asciidoctor.Asciidoctor
import org.asciidoctor.AttributesBuilder
import org.asciidoctor.OptionsBuilder
import org.asciidoctor.SafeMode
import org.jboss.arquillian.spock.ArquillianSputnik
import org.jboss.arquillian.test.api.ArquillianResource
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import spock.lang.Specification

@RunWith(ArquillianSputnik)
class WhenDitaaDiagramIsRendered extends Specification {

    static final String ASCIIDOCTOR_DIAGRAM = 'asciidoctor-diagram'

    static final String BUILD_DIR = 'build'

    @ArquillianResource
    private Asciidoctor asciidoctor

    @ArquillianResource
    public TemporaryFolder testFolder

    def 'should render ditaa diagram to HTML'() throws Exception {

        given:
        String imageFileName = UUID.randomUUID()
        File imagesOutDir = testFolder.newFolder()
        File createdImage = new File(imagesOutDir, "${imageFileName}.png")
        def createdCacheImage = new File(testFolder.root, ".asciidoctor/diagram/${imageFileName}.png.cache")

        String document = """= Document Title

Hello World

[ditaa,${imageFileName}]
....

+---+
| A |
+---+
....

"""

        asciidoctor.requireLibrary(ASCIIDOCTOR_DIAGRAM)

        when:
        String result = asciidoctor.convert(document, OptionsBuilder.options()
                .toFile(false)
                .toDir(testFolder.root)
                .safe(SafeMode.UNSAFE)
                .attributes(AttributesBuilder.attributes()
                        .attribute('imagesdir', imagesOutDir.absolutePath)
                        .attribute('outdir', testFolder.root.absolutePath)))


        then:
        result.contains("""src="${imagesOutDir.absolutePath}/${imageFileName}.png""")

        createdImage.exists()
        createdCacheImage.exists()

    }

    def 'should render ditaa diagram to PDF'() throws Exception {

        given:
        File destinationFile = new File('test.pdf')
        String imageFileName = UUID.randomUUID()

        File createdPdf = new File(testFolder.root, destinationFile.name)
        File createdImage = new File(testFolder.root, "${imageFileName}.png")
        File createdCacheImage = new File(testFolder.root, ".asciidoctor/diagram/${imageFileName}.png.cache")

        String document = """= Document Title

Hello World

[ditaa,${imageFileName}]
....

+---+
| A |
+---+
....

"""

        asciidoctor.requireLibrary(ASCIIDOCTOR_DIAGRAM)

        when:
        asciidoctor.convert(document, OptionsBuilder.options()
                .toDir(testFolder.root)
                .toFile(destinationFile)
                .safe(SafeMode.UNSAFE)
                .backend('pdf'))


        then:

        createdPdf.exists()
        createdImage.exists()
        createdCacheImage.exists()

    }

}