package latexEngine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

public class formulaToImage {

    /**
     * Converts a LaTeX string to an image and optionally opens it.
     *
     * @param latex      The LaTeX string to render
     * @param outputPath Path to save the PNG file
     * @param fontSize   Font size of the formula
     */
    public static void convertAndOpen(String latex, String outputPath, float fontSize) {
        try {
            // Create TeX formula
            TeXFormula formula = new TeXFormula(latex);

            // Render image with black text on white background
            BufferedImage image = (BufferedImage) formula.createBufferedImage(
                    TeXConstants.STYLE_DISPLAY, // Display style
                    fontSize,
                    Color.BLACK,                // Text color
                    Color.WHITE                 // Background color
            );

            // Save as PNG
            ImageIO.write(image, "png", new File(outputPath));
            System.out.println("Generated image: " + outputPath);

            // Open image automatically (cross-platform)
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + outputPath);
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + outputPath);
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec("xdg-open " + outputPath);
            }

        } catch (Exception e) {
            System.out.println("Error generating image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Example usage
    public static void main(String[] args) {
        String latex = "\\frac{a^2 + b^2}{c}";
        String outputPath = "formula.png";
        float fontSize = 20f;

        convertAndOpen(latex, outputPath, fontSize);
    }
}
