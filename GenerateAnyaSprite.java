import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.io.File;

public class GenerateAnyaSprite {
    public static void main(String[] args) throws Exception {
        // Create a simple sprite for Anya (64x64 pixels to match tile size)
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // Fill background with transparent-like color
        g.setColor(new Color(200, 100, 200)); // Magenta/purple color to distinguish from enemy
        g.fillRect(0, 0, 64, 64);
        
        // Draw a simple character shape (head + body + gun)
        g.setColor(new Color(255, 200, 100)); // Skin tone for head
        g.fillOval(16, 8, 20, 20); // Head
        
        g.setColor(new Color(100, 150, 200)); // Blue shirt
        g.fillRect(14, 28, 24, 20); // Body
        
        g.setColor(Color.BLACK); // Eyes
        g.fillOval(19, 13, 3, 3);
        g.fillOval(28, 13, 3, 3);
        
        // Draw a gun/weapon
        g.setColor(Color.GRAY);
        g.fillRect(38, 30, 18, 8); // Gun barrel
        g.fillRect(42, 24, 8, 8); // Gun body
        
        // Draw legs
        g.setColor(new Color(100, 100, 100)); // Dark pants
        g.fillRect(18, 48, 6, 14);
        g.fillRect(28, 48, 6, 14);
        
        // Save the image
        ImageIO.write(image, "png", new File("src/main/java/platformer/gfx/Anya.png"));
        System.out.println("Anya sprite generated successfully!");
    }
}
