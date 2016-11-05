package net.sf.odinms.net.channel;

import  javax.swing.*; 
import  java.awt.*; 
import  java.awt.event.*; 
import  java.awt.event.ActionEvent; 

/**
 * 
 * @author yuezhang
 * @QQ 237253995.
 */

public  class  Kils  { 
   JFrame  frame; 
   JButton  button; 
   Font  font; 
 
   public  Kils(){ 
       font  =  new  Font("黑体",0,12); 
       UIManager.put("Button.font",font); 
       UIManager.put("Label.font",font); 
       frame  =  new  JFrame("警告"); 
       button  =  new  JButton("请勿继续使用未认证版本,购买请联系 乐章QQ：237253995"); 
       button.addActionListener(new  ActionListener()  { 
           
           
           public  void  actionPerformed(ActionEvent  actionEvent)  { 
               JLabel  l  =  new  JLabel(""); 
               JOptionPane.showMessageDialog(frame,l,"警告",JOptionPane.ERROR_MESSAGE); 
               System.exit(0);
           } 
       }); 
       frame.getContentPane().add(button); 
       frame.setSize(400,  300); 
       frame.setVisible(true); 
   } 
   public  static  void  main(String[]  args)  { 
       new  Kils(); 
   } 
}
