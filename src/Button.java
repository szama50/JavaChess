import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

interface ButtonFunction {
    void onClick();
}

class Button {
    private final JButton button;
    private JPanel parent;
    private ButtonFunction buttonFunction;
    Button(String name,int posX, int posY,int sizeX, int sizeY, JPanel btnParent, ButtonFunction buttonFun) {
        button = new JButton(name);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setForeground(Color.BLACK);
        button.setBackground(Color.RED);
        Font font = new Font("Arial",Font.BOLD,40);
        button.setFont(font);
        buttonFunction = buttonFun;
        button.setBounds(posX,posY,sizeX,sizeY);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonFunction.onClick();
            }
        });
        parent = btnParent;
        parent.add(button);
    }
    public void removeFromParent() {
        parent.remove(button);
    }
}