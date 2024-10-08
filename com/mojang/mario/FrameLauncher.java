package com.mojang.mario;

import java.awt.*;
import javax.swing.*;

import com.mojang.mario.*;

public class FrameLauncher
{
    public static void main(String[] args)
    {
        MarioComponent mario = new MarioComponent(640, 480);
        JFrame frame = new JFrame("Mario Inheritance");
        frame.setContentPane(mario);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width-frame.getWidth())/2, (screenSize.height-frame.getHeight())/2);
        
        frame.setVisible(true);
        mario.start();
    }
}