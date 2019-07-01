package com.flyzebra.filemanager.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

public class BitmapTools {
	public static Bitmap GetMinBitmapFormFile(String spath,int max){
		//获取缩略图
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.ARGB_4444; 
		opts.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(spath, opts);	
		//这里返回的bmp是null
//		int height = opts.outHeight * width / opts.outWidth;	
		int h = opts.outHeight; 
        int w = opts.outWidth;
		if(h>w){
			opts.outHeight = max;
			opts.outWidth = w * max / h;
		}else{
			opts.outHeight = h * max / w;
			opts.outWidth = max;
		}
		h = opts.outHeight;
		w = opts.outWidth;
		opts.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeFile(spath, opts);
		Bitmap bm = ThumbnailUtils.extractThumbnail(bmp, w, h, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		bmp.recycle();
		return bm;
	}
	
	public static Bitmap getImageThumbnail(String spath, int width,  int height) { 
        Bitmap bitmap = null; 
        BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inPreferredConfig = Bitmap.Config.ARGB_4444; 
        options.inJustDecodeBounds = true; 
        // 获取这个图片的宽和高，注意此处的bitmap为null 
        bitmap = BitmapFactory.decodeFile(spath, options); 
        options.inJustDecodeBounds = false; // 设为 false 
        // 计算缩放比 
        int h = options.outHeight; 
        int w = options.outWidth; 
        int beWidth = w / width; 
        int beHeight = h / height; 
        int be = 1; 
        if (beWidth < beHeight) { 
            be = beWidth; 
        } else { 
            be = beHeight; 
        } 
        if (be <= 0) { 
            be = 1; 
        } 
        options.inSampleSize = be; 
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false 
        bitmap = BitmapFactory.decodeFile(spath, options); 
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象 
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,ThumbnailUtils.OPTIONS_RECYCLE_INPUT); 
        return bitmap; 
    } 
	
	public static  Bitmap getVideoThumbnail(String videoPath, int width, int height) {  
        Bitmap bitmap = null;  
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MICRO_KIND); 
        if(bitmap != null){
        	bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        }
        return bitmap;  
    }

}
