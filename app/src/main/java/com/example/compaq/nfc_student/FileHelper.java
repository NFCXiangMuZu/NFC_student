package com.example.compaq.nfc_student;

/**
 * 文件相关操作集合类
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import static android.provider.MediaStore.Files.*;

public class FileHelper {
	private Context context;
	/** SD卡是否存在**/
	private boolean hasSD = false;
	/** 当前程序包的路径**/
	private String FILESPATH;

	public FileHelper(Context context) {
		this.context = context;
		hasSD = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		FILESPATH = this.context.getFilesDir().getPath();
	}

	/**
	 * 在SD卡上创建文件
	 * @throws IOException
	 */
	public static File createSDFile(String fileName) throws IOException {
		File file = new File(StaticValue.SDPATH + "//" + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * 删除SD卡上的文件
	 * @param fileName
	 */
	public static boolean deleteSDFile(String fileName) {
		File file = new File(StaticValue.SDPATH + "//" + fileName);
		if (file == null || !file.exists() || file.isDirectory())
			return false;
		return file.delete();
	}

	/**
	 * 写入内容到SD卡中的txt文本中
	 * str为内容
	 */
	public static void writeSDFile(String str,String fileName)
	{
		File f = new File(StaticValue.SDPATH + "//" + fileName);
		try {
			//第二个参数意义是说是否以append方式添加内容
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			bw.write(str);
			bw.write("\r\n");
			bw.flush();
			System.out.println("写入成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取SD卡中文本文件
	 * @param fileName
	 * @return
	 */
	public static List<String> readSDFile(String fileName) {

		List<String> list=new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readline = "";

			while ((readline = br.readLine()) != null) {
				System.out.println("readline:" + readline);
				list.add(readline);
				sb.append(readline);
			}
			br.close();
			System.out.println("读取成功：" + sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 删除SD卡中文本文件的某一行内容
	 * @param fileName
	 * @return
	 */
	public static List<String> Delete_oneFile(String fileName,int del_num) {

		List<String> list=new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readline = "";

			while ((readline = br.readLine()) != null) {
				System.out.println("readline:" + readline);
				list.add(readline);
				sb.append(readline);
			}

			list.remove(del_num);

			System.out.println("读取成功：" + sb.toString());

			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 创建新的文件目录
	 * @param path 要创建的文件目录
	 * @return
	 */
	public static boolean mkDir(String path) {
		File dir = new File(path);
		boolean res = dir.mkdirs();

		return res;
	}

	/**
	 * 复制文件操作
	 * @param fromFile 文件传输源目录
	 * @param toFile 文件传输目的目录
	 * @return
	 */
	public static boolean CopyFile(String fromFile, String toFile) {
		try {
			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[4096];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			bt = null;
			return true;

		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 删除文件
	 * @param path 被删除文件目录
	 * @return
	 */
	public static boolean deleteFile(String path) {
		try {
			File file = new File(path);
			return file.delete();
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 *待压缩的文件数组，只能是文件，不能包含文件夹
	 *生成压缩文件名，例如/local/temp/test.zip
	 * @throws IOException
	 */
	public static void zip(List<String> files, String zipFile) throws IOException {
		BufferedInputStream origin = null;
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(zipFile)));
		byte data[] = null;
		FileInputStream fi = null;
		ZipEntry entry = null;
		try {
			data = new byte[1024];

			for (int i = 0; i < files.size(); i++) {
				fi = new FileInputStream(files.get(i));
				origin = new BufferedInputStream(fi, 1024);
				try {
					entry = new ZipEntry(files.get(i).substring(files.get(i)
							.lastIndexOf("/") + 1));
					out.putNextEntry(entry);
					int count;
					while ((count = origin.read(data, 0, 1024)) != -1) {
						out.write(data, 0, count);
					}
				} finally {
					entry = null;
					origin.close();
					origin = null;
				}
			}
		} finally {
			fi = null;
			data = null;
			out.close();
			out = null;
		}
	}

	/**
	 * 获取特定格式文件
	 * @return
	 */
	public static List<String> getSpecificTypeOfFile(Context context,String[] extension)
	{

		List<String> file_list = new ArrayList<>();
		//从外存中获取
		Uri fileUri= getContentUri("external");
		//筛选列，这里只筛选了：文件路径和不含后缀的文件名
		String[] projection=new String[]{
				FileColumns.DATA, FileColumns.TITLE
		};
		//构造筛选语句
		String selection="";
		for(int i=0;i<extension.length;i++)
		{
			if(i!=0)
			{
				selection=selection+" OR ";
			}
			selection=selection+ FileColumns.DATA+" LIKE '%"+extension[i]+"'";
		}
		//按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
		String sortOrder= FileColumns.DATE_MODIFIED;
		//获取内容解析器对象
		ContentResolver resolver=context.getContentResolver();
		//获取游标
		Cursor cursor=resolver.query(fileUri, projection, selection, null, sortOrder);
		if(cursor==null)
			return null;
		//游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
		if(cursor.moveToLast())
		{
			do{
				//输出文件的完整路径
				String data=cursor.getString(0);
				file_list.add(data);
				Log.d("tag", data);
			}while(cursor.moveToPrevious());
		}
		cursor.close();
		return file_list;

	}
} 
