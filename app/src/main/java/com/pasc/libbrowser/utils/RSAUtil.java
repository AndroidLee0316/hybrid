package com.pasc.libbrowser.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public final class RSAUtil
{
  private static String RSA = "RSA";
  /**加密方式，标准jdk的*/
  public static final String TRANSFORMATION = "RSA/None/NoPadding";

  /**
   * 随机生成RSA密钥对(默认密钥长度为1024)
   *
   * @return
   */
  public static KeyPair generateRSAKeyPair()
  {
    return generateRSAKeyPair(1024);
  }

  /**
   * 随机生成RSA密钥对
   *
   * @param keyLength
   *            密钥长度，范围：512～2048<br>
   *            一般1024
   * @return
   */
  public static KeyPair generateRSAKeyPair(int keyLength)
  {
    try
    {
      KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
      kpg.initialize(keyLength);
      return kpg.genKeyPair();
    } catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
      return null;
    }
  }

  /** 使用公钥加密 */
  public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
    // 得到公钥对象
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PublicKey pubKey = keyFactory.generatePublic(keySpec);
    // 加密数据
    Cipher cp = Cipher.getInstance(RSA);
    cp.init(Cipher.ENCRYPT_MODE, pubKey);
    return cp.doFinal(data);
  }

  /** 使用私钥解密 */
  public static byte[] decryptByPrivateKey(byte[] encrypted, byte[] privateKey) throws Exception {
    // 得到私钥对象
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
    KeyFactory kf = KeyFactory.getInstance(RSA);
    PrivateKey keyPrivate = kf.generatePrivate(keySpec);
    // 解密数据
    Cipher cp = Cipher.getInstance(RSA);
    cp.init(Cipher.DECRYPT_MODE, keyPrivate);
    byte[] arr = cp.doFinal(encrypted);
    return arr;
  }

  /**
   * 通过公钥byte[](publicKey.getEncoded())将公钥还原，适用于RSA算法
   *
   * @param keyBytes
   * @return
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public static PublicKey getPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException,
      InvalidKeySpecException
  {
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(RSA);
    PublicKey publicKey = keyFactory.generatePublic(keySpec);
    return publicKey;
  }

  /**
   * 通过私钥byte[]将公钥还原，适用于RSA算法
   *
   * @param keyBytes
   * @return
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public static PrivateKey getPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException,
      InvalidKeySpecException
  {
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(RSA);
    PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
    return privateKey;
  }

  /**
   * 使用N、e值还原公钥
   *
   * @param modulus
   * @param publicExponent
   * @return
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public static PublicKey getPublicKey(String modulus, String publicExponent)
      throws NoSuchAlgorithmException, InvalidKeySpecException
  {
    BigInteger bigIntModulus = new BigInteger(modulus);
    BigInteger bigIntPrivateExponent = new BigInteger(publicExponent);
    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
    KeyFactory keyFactory = KeyFactory.getInstance(RSA);
    PublicKey publicKey = keyFactory.generatePublic(keySpec);
    return publicKey;
  }

  /**
   * 使用N、d值还原私钥
   *
   * @param modulus
   * @param privateExponent
   * @return
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public static PrivateKey getPrivateKey(String modulus, String privateExponent)
      throws NoSuchAlgorithmException, InvalidKeySpecException
  {
    BigInteger bigIntModulus = new BigInteger(modulus);
    BigInteger bigIntPrivateExponent = new BigInteger(privateExponent);
    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
    KeyFactory keyFactory = KeyFactory.getInstance(RSA);
    PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
    return privateKey;
  }

  /**
   * 从字符串中加载公钥
   *
   * @param publicKeyStr
   *            公钥数据字符串
   * @throws Exception
   *             加载公钥时产生的异常
   */
  public static PublicKey loadPublicKey(String publicKeyStr) throws Exception
  {
    try
    {
      byte[] buffer = Base64Util.decode(publicKeyStr);
      KeyFactory keyFactory = KeyFactory.getInstance(RSA);
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
      return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    } catch (NoSuchAlgorithmException e)
    {
      throw new Exception("无此算法");
    } catch (InvalidKeySpecException e)
    {
      throw new Exception("公钥非法");
    } catch (NullPointerException e)
    {
      throw new Exception("公钥数据为空");
    }
  }

  /**
   * 从字符串中加载私钥<br>
   * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
   *
   * @param privateKeyStr
   * @return
   * @throws Exception
   */
  public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception
  {
    try
    {
      byte[] buffer = Base64Util.decode(privateKeyStr);
      // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
      KeyFactory keyFactory = KeyFactory.getInstance(RSA);
      return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    } catch (NoSuchAlgorithmException e)
    {
      throw new Exception("无此算法");
    } catch (InvalidKeySpecException e)
    {
      throw new Exception("私钥非法");
    } catch (NullPointerException e)
    {
      throw new Exception("私钥数据为空");
    }
  }

  /**
   * 从文件中输入流中加载公钥
   *
   * @param in
   *            公钥输入流
   * @throws Exception
   *             加载公钥时产生的异常
   */
  public static PublicKey loadPublicKey(InputStream in) throws Exception
  {
    try
    {
      return loadPublicKey(readKey(in));
    } catch (IOException e)
    {
      throw new Exception("公钥数据流读取错误");
    } catch (NullPointerException e)
    {
      throw new Exception("公钥输入流为空");
    }
  }

  /**
   * 从文件中加载私钥
   *
   * @param keyFileName
   *            私钥文件名
   * @return 是否成功
   * @throws Exception
   */
  public static PrivateKey loadPrivateKey(InputStream in) throws Exception
  {
    try
    {
      return loadPrivateKey(readKey(in));
    } catch (IOException e)
    {
      throw new Exception("私钥数据读取错误");
    } catch (NullPointerException e)
    {
      throw new Exception("私钥输入流为空");
    }
  }

  /**
   * 读取密钥信息
   *
   * @param in
   * @return
   * @throws IOException
   */
  private static String readKey(InputStream in) throws IOException
  {
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    String readLine = null;
    StringBuilder sb = new StringBuilder();
    while ((readLine = br.readLine()) != null)
    {
      if (readLine.charAt(0) == '-')
      {
        continue;
      } else
      {
        sb.append(readLine);
        sb.append('\r');
      }
    }

    return sb.toString();
  }

  /**
   * 打印公钥信息
   *
   * @param publicKey
   */
  public static void printPublicKeyInfo(PublicKey publicKey)
  {
    RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
    System.out.println("----------RSAPublicKey----------");
    System.out.println("Modulus.length=" + rsaPublicKey.getModulus().bitLength());
    System.out.println("Modulus=" + rsaPublicKey.getModulus().toString());
    System.out.println("PublicExponent.length=" + rsaPublicKey.getPublicExponent().bitLength());
    System.out.println("PublicExponent=" + rsaPublicKey.getPublicExponent().toString());
  }

  public static void printPrivateKeyInfo(PrivateKey privateKey)
  {
    RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
    System.out.println("----------RSAPrivateKey ----------");
    System.out.println("Modulus.length=" + rsaPrivateKey.getModulus().bitLength());
    System.out.println("Modulus=" + rsaPrivateKey.getModulus().toString());
    System.out.println("PrivateExponent.length=" + rsaPrivateKey.getPrivateExponent().bitLength());
    System.out.println("PrivatecExponent=" + rsaPrivateKey.getPrivateExponent().toString());

  }

}