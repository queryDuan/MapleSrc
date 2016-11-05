package net.sf.odinms.net.world.remote;

 import java.security.*; 
 import javax.crypto.*; 
 public class NewClass { 
 public static void main(String[] args){ 
    NewClass my=new NewClass(); 
    my.run(); 
  } 
 public  void run() { 
 // 添加新安全算法 , 如果用 JCE 就要把它添加进去
 Security.addProvider(new com.sun.crypto.provider.SunJCE()); 
 String Algorithm="DES"; // 定义 加密算法 , 可用 DES,DESede,Blowfish 
 String myinfo="13212345"; 
   try { 
   // 生成密钥
   KeyGenerator keygen = KeyGenerator.getInstance(Algorithm); 
   SecretKey deskey = keygen.generateKey(); 
   // 加密
   System.out.println("加密前的二进串 :"+byte2hex(myinfo.getBytes())); 
   System.out.println("加密前的信息 :"+myinfo); 
   Cipher c1 = Cipher.getInstance(Algorithm); 
   c1.init(Cipher.ENCRYPT_MODE,deskey); 
   byte[] cipherByte=c1.doFinal(myinfo.getBytes()); 
    System.out.println("加密后的二进串 :"+byte2hex(cipherByte)); 
   // 解密
   c1 = Cipher.getInstance(Algorithm); 
   c1.init(Cipher.DECRYPT_MODE,deskey); 
   byte[] clearByte=c1.doFinal(cipherByte); 
   System.out.println("解密后的二进串 :"+byte2hex(clearByte)); 
   System.out.println("解密后的信息 :"+(new String(clearByte))); 
  } 
   catch (java.security.NoSuchAlgorithmException e1) {e1.printStackTrace();} 
   catch (javax.crypto.NoSuchPaddingException e2) {e2.printStackTrace();} 
   catch (java.lang.Exception e3) {e3.printStackTrace();} 
  } 
 public String byte2hex(byte[] b) // 二行制转字符串
    { 
     String hs=""; 
     String stmp=""; 
     for (int n=0;n<b.length;n++) 
      { 
       stmp=(java.lang.Integer.toHexString(b[n] & 0XFF)); 
       if (stmp.length()==1) hs=hs+"0"+stmp; 
       else hs=hs+stmp; 
       if (n<b.length-1)  hs=hs+":"; 
      } 
     return hs.toUpperCase(); 
    } 
 } 


