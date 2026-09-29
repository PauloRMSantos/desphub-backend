package desphub.pds.backend.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentPdfRenderer {

    private static final float MARGIN = 56;
    private static final float FONT_SIZE = 11;
    private static final float LEADING = 16;

    public byte[] render(String content) {
        String text = content == null ? "" : content;
        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            float usableWidth = PDRectangle.A4.getWidth() - 2 * MARGIN;

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(doc, page);
            float y = PDRectangle.A4.getHeight() - MARGIN;

            for (String rawLine : text.split("\n", -1)) {
                String line = sanitize(rawLine);
                if (line.isBlank()) {
                    y -= LEADING;
                    if (y < MARGIN) {
                        cs.close();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        cs = new PDPageContentStream(doc, page);
                        y = PDRectangle.A4.getHeight() - MARGIN;
                    }
                    continue;
                }
                for (String wrapped : wrap(line, font, usableWidth)) {
                    if (y < MARGIN) {
                        cs.close();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        cs = new PDPageContentStream(doc, page);
                        y = PDRectangle.A4.getHeight() - MARGIN;
                    }
                    cs.beginText();
                    cs.setFont(font, FONT_SIZE);
                    cs.newLineAtOffset(MARGIN, y);
                    cs.showText(wrapped);
                    cs.endText();
                    y -= LEADING;
                }
            }

            cs.close();
            doc.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao gerar o PDF", e);
        }
    }

    private List<String> wrap(String line, PDType1Font font, float maxWidth) throws IOException {
        List<String> out = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : line.split(" ")) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            if (width(font, candidate) <= maxWidth) {
                current.setLength(0);
                current.append(candidate);
            } else {
                if (!current.isEmpty()) {
                    out.add(current.toString());
                    current.setLength(0);
                }
                if (width(font, word) > maxWidth) {
                    StringBuilder chunk = new StringBuilder();
                    for (char c : word.toCharArray()) {
                        if (width(font, chunk.toString() + c) > maxWidth && chunk.length() > 0) {
                            out.add(chunk.toString());
                            chunk.setLength(0);
                        }
                        chunk.append(c);
                    }
                    current.append(chunk);
                } else {
                    current.append(word);
                }
            }
        }
        if (!current.isEmpty()) {
            out.add(current.toString());
        }
        return out;
    }

    private float width(PDType1Font font, String text) throws IOException {
        return font.getStringWidth(text) / 1000 * FONT_SIZE;
    }

    private String sanitize(String s) {
        String t = s.replace("\t", "    ")
                .replace("‘", "'").replace("’", "'").replace("‛", "'")
                .replace("“", "\"").replace("”", "\"")
                .replace("–", "-").replace("—", "-")
                .replace("…", "...");
        StringBuilder sb = new StringBuilder(t.length());
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (c >= 0x20 && c <= 0xFF && !(c >= 0x7F && c <= 0x9F)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
