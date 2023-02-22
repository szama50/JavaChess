import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

class Text {
    private JPanel parent;
    private JTextField text;
    Text(String content,int posX, int posY,int sizeX, int sizeY, JPanel textParent,int fontSize) {
        text = new JTextField();
        text.setEditable(false);
        text.setBounds(posX,posY,sizeX,sizeY);
        text.setText(content);
        text.setForeground(Color.RED);
        text.setBackground(Color.BLACK);
        text.setHorizontalAlignment(JTextField.CENTER);
        text.setBorder(BorderFactory.createEmptyBorder());

        Font font = new Font("Arial",Font.BOLD,fontSize);
        text.setFont(font);
        try {
            Font font2 = Font.createFont(Font.TRUETYPE_FONT,
                    new FileInputStream(new File("custom.ttf"))).deriveFont(Font.BOLD,fontSize);
            text.setFont(font2);
        }
        catch(Exception e) {return;};

        parent = textParent;
        parent.add(text);
    }
    public void removeFromParent() { parent.remove(text); }
}
