package com.flyzebra.filemanager.activity;

import java.util.HashMap;
import java.util.List;

import com.flyzebra.filemanager.R;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainListAdapter extends BaseAdapter {
	private List<HashMap<String, Object>> list = null;
	private String key[] = null;
	private int id[] = null;
	private int idListview;
	private LayoutInflater inflater = null;
	private OnCheckBoxClick mOnCheckClick;
	private MyApp myapp;

	private final int VIEW_LIST1 = 1;
	private final int VIEW_GRID4 = 2;
	private final int VIEW_GRID5 = 3;
	private final int VIEW_GRID6 = 4;

	/* 文件类型,数字为奇数为选中状态，程序中只以+1 -1 表示 */
	private final int TYPE_ROOT = 0;
	private final int TYPE_SDCARD = 2;
	private final int TYPE_DIR1 = 4;
	private final int TYPE_DIR1_NET = 6;
	private final int TYPE_DIR2 = 52;
	private final int TYPE_DIR2_NET = 54;
	private final int TYPE_FILE = 56;

	private class ViewHolder {
		public ImageView iv01 = null;
		public TextView tv01 = null;
		public TextView tv02 = null;
		public RelativeLayout lr01 = null;
		public CheckBox ck01 = null;
	}

	// 定义一个接口回调单击CheckBox事件

	public interface OnCheckBoxClick {
		public void OnItemCheckBoxClick(View v);
	}

	public MainListAdapter(Context context, List<HashMap<String, Object>> list,
			int idListview, String[] key, int[] id, MyApp myapp,
			OnCheckBoxClick mOnCheckClick) {
		inflater = LayoutInflater.from(context);
		this.myapp = myapp;
		this.idListview = idListview;
		this.mOnCheckClick = mOnCheckClick;
		this.list = list;
		this.key = new String[key.length];
		this.id = new int[id.length];
		System.arraycopy(key, 0, this.key, 0, key.length);
		System.arraycopy(id, 0, this.id, 0, id.length);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(idListview, null);
			holder.iv01 = (ImageView) convertView.findViewById(id[0]);
			holder.tv01 = (TextView) convertView.findViewById(id[1]);
			holder.tv02 = (TextView) convertView.findViewById(id[2]);
			holder.lr01 = (RelativeLayout) convertView.findViewById(id[3]);
			holder.ck01 = (CheckBox) convertView.findViewById(id[4]);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.ck01.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnCheckClick.OnItemCheckBoxClick(v);
			}
		});
		holder.ck01.setTag(position);

		// 获取类型
		int TYPE = (Integer) list.get(position).get("MARK");
		// 设置图标背景
		if (TYPE % 2 == 1) {
			holder.lr01.setBackgroundColor(Color.BLUE);
			holder.ck01.setChecked(true);
			TYPE--;
		} else {
			holder.lr01.setBackgroundColor(Color.WHITE);
			holder.ck01.setChecked(false);
		}

		// 设置图标
		switch (TYPE) {
		case TYPE_ROOT:
			holder.iv01.setBackgroundResource(R.drawable.icon_root);
			break;
		case TYPE_SDCARD:
			holder.iv01.setBackgroundResource(R.drawable.icon_card);
			break;
		case TYPE_DIR1:
			holder.iv01.setBackgroundResource(R.drawable.icon_mobile);
			break;
		case TYPE_DIR1_NET:
			holder.iv01.setBackgroundResource(R.drawable.icon_network);
			break;
		case TYPE_DIR2:
		case TYPE_DIR2_NET:
			switch (myapp.getShowview()) {
			case VIEW_LIST1:
				holder.iv01.setBackgroundResource(R.drawable.icon_dirg1);
				break;
			case VIEW_GRID4:
				holder.iv01.setBackgroundResource(R.drawable.icon_dirg1);
				break;
			case VIEW_GRID5:
				holder.iv01.setBackgroundResource(R.drawable.icon_dirg2);
				break;
			case VIEW_GRID6:
				holder.iv01.setBackgroundResource(R.drawable.icon_dirg3);
				break;
			}
			break;
		case TYPE_FILE:
			String filename = (String) myapp.getList().get(position)
					.get("NAME");
			int last = filename.lastIndexOf(".");
			if (last < 0) {
				switch (myapp.getShowview()) {
				case VIEW_LIST1:
					holder.iv01.setBackgroundResource(R.drawable.icon_file1);
					break;
				case VIEW_GRID4:
					holder.iv01.setBackgroundResource(R.drawable.icon_file1);
					break;
				case VIEW_GRID5:
					holder.iv01.setBackgroundResource(R.drawable.icon_file2);
					break;
				case VIEW_GRID6:
					holder.iv01.setBackgroundResource(R.drawable.icon_file3);
					break;
				}
			} else {
				String filetype = filename.substring(last, filename.length());
				if (filetype.equalsIgnoreCase(".mp3")) {
					Log.i("FILETYEP", filetype);
					switch (myapp.getShowview()) {
					case VIEW_LIST1:
						holder.iv01.setBackgroundResource(R.drawable.icon_mp31);
						break;
					case VIEW_GRID4:
						holder.iv01.setBackgroundResource(R.drawable.icon_mp31);
						break;
					case VIEW_GRID5:
						holder.iv01.setBackgroundResource(R.drawable.icon_mp32);
						break;
					case VIEW_GRID6:
						holder.iv01.setBackgroundResource(R.drawable.icon_mp33);
						break;
					}
				} else if (filetype.equalsIgnoreCase(".wma")
						|| filetype.equalsIgnoreCase(".wav")) {
					switch (myapp.getShowview()) {
					case VIEW_LIST1:
						holder.iv01
								.setBackgroundResource(R.drawable.icon_audio1);
						break;
					case VIEW_GRID4:
						holder.iv01
								.setBackgroundResource(R.drawable.icon_audio1);
						break;
					case VIEW_GRID5:
						holder.iv01
								.setBackgroundResource(R.drawable.icon_audio2);
						break;
					case VIEW_GRID6:
						holder.iv01
								.setBackgroundResource(R.drawable.icon_audio3);
						break;
					}
				} else if (filetype.equalsIgnoreCase(".rmvb")
						|| filetype.equalsIgnoreCase(".rm")
						|| filetype.equalsIgnoreCase(".avi")
						|| filetype.equalsIgnoreCase(".wmv")
						|| filetype.equalsIgnoreCase(".mp4")) {
					Log.i("FILETYEP", filetype);
					switch (myapp.getShowview()) {
					case VIEW_LIST1:
						holder.iv01
								.setBackgroundResource(R.drawable.icon_video1);
						break;
					case VIEW_GRID4:
						holder.iv01
								.setBackgroundResource(R.drawable.icon_video1);
						break;
					case VIEW_GRID5:
						holder.iv01
								.setBackgroundResource(R.drawable.icon_video2);
						break;
					case VIEW_GRID6:
						holder.iv01
								.setBackgroundResource(R.drawable.icon_video3);
						break;
					}
				} else if (filetype.equalsIgnoreCase(".pdf")) {
					switch (myapp.getShowview()) {
					case VIEW_LIST1:
						holder.iv01.setBackgroundResource(R.drawable.icon_pdf1);
						break;
					case VIEW_GRID4:
						holder.iv01.setBackgroundResource(R.drawable.icon_pdf1);
						break;
					case VIEW_GRID5:
						holder.iv01.setBackgroundResource(R.drawable.icon_pdf2);
						break;
					case VIEW_GRID6:
						holder.iv01.setBackgroundResource(R.drawable.icon_pdf3);
						break;
					}
				} else if (filetype.equalsIgnoreCase(".txt")
						|| filetype.equalsIgnoreCase(".doc")
						|| filetype.equalsIgnoreCase(".wps")
						|| filetype.equalsIgnoreCase(".xml")) {
					switch (myapp.getShowview()) {
					case VIEW_LIST1:
						holder.iv01.setBackgroundResource(R.drawable.icon_txt1);
						break;
					case VIEW_GRID4:
						holder.iv01.setBackgroundResource(R.drawable.icon_txt1);
						break;
					case VIEW_GRID5:
						holder.iv01.setBackgroundResource(R.drawable.icon_txt2);
						break;
					case VIEW_GRID6:
						holder.iv01.setBackgroundResource(R.drawable.icon_txt3);
						break;
					}
				} else {
					switch (myapp.getShowview()) {
					case VIEW_LIST1:
						holder.iv01
								.setBackgroundResource(R.drawable.icon_file1);
						break;
					case VIEW_GRID4:
						holder.iv01
								.setBackgroundResource(R.drawable.icon_file1);
						break;
					case VIEW_GRID5:
						holder.iv01
								.setBackgroundResource(R.drawable.icon_file2);
						break;
					case VIEW_GRID6:
						holder.iv01
								.setBackgroundResource(R.drawable.icon_file3);
						break;
					}
				}
				break;

			}
		}
		holder.tv01.setText((String) list.get(position).get(key[1]));
		holder.tv02.setText((String) list.get(position).get(key[2]));
		return convertView;
	}
	
}

//    import java.io.File;  
//    import android.app.Activity;  
//    import android.graphics.Bitmap;  
//    import android.graphics.BitmapFactory;  
//    import android.media.ThumbnailUtils;  
//    import android.os.Bundle;  
//    import android.os.Environment;  
//    import android.provider.MediaStore;  
//    import android.widget.ImageView;  
//    /** 
//     * 获取图片和视频的缩略图 
//     * 这两个方法必须在2.2及以上版本使用，因为其中使用了ThumbnailUtils这个类 
//     */  
//    public class AndroidTestActivity extends Activity {  
//        private ImageView imageThumbnail;  
//        private ImageView videoThumbnail;  
//      
//        /** Called when the activity is first created. */  
//        @Override  
//        public void onCreate(Bundle savedInstanceState) {  
//            super.onCreate(savedInstanceState);  
//            setContentView(R.layout.main);  
//      
//            imageThumbnail = (ImageView) findViewById(R.id.image_thumbnail);  
//            videoThumbnail = (ImageView) findViewById(R.id.video_thumbnail);  
//      
//            String imagePath = Environment.getExternalStorageDirectory()  
//                    .getAbsolutePath()  
//                    + File.separator  
//                    + "photo"  
//                    + File.separator  
//                    + "yexuan.jpg";  
//      
//            String videoPath = Environment.getExternalStorageDirectory()  
//                    .getAbsolutePath()  
//                    + File.separator  
//                    + "video"  
//                    + File.separator  
//                    + "醋点灯.avi";  
//              
//            imageThumbnail.setImageBitmap(getImageThumbnail(imagePath, 60, 60));  
//            videoThumbnail.setImageBitmap(getVideoThumbnail(videoPath, 60, 60,  
//                    MediaStore.Images.Thumbnails.MICRO_KIND));  
//        }  
//      
//        /** 
//         * 根据指定的图像路径和大小来获取缩略图 
//         * 此方法有两点好处： 
//         *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度， 
//         *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 
//         *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 
//         *        用这个工具生成的图像不会被拉伸。 
//         * @param imagePath 图像的路径 
//         * @param width 指定输出图像的宽度 
//         * @param height 指定输出图像的高度 
//         * @return 生成的缩略图 
//         */  
//        private Bitmap getImageThumbnail(String imagePath, int width, int height) {  
//            Bitmap bitmap = null;  
//            BitmapFactory.Options options = new BitmapFactory.Options();  
//            options.inJustDecodeBounds = true;  
//            // 获取这个图片的宽和高，注意此处的bitmap为null  
//            bitmap = BitmapFactory.decodeFile(imagePath, options);  
//            options.inJustDecodeBounds = false; // 设为 false  
//            // 计算缩放比  
//            int h = options.outHeight;  
//            int w = options.outWidth;  
//            int beWidth = w / width;  
//            int beHeight = h / height;  
//            int be = 1;  
//            if (beWidth < beHeight) {  
//                be = beWidth;  
//            } else {  
//                be = beHeight;  
//            }  
//            if (be <= 0) {  
//                be = 1;  
//            }  
//            options.inSampleSize = be;  
//            // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false  
//            bitmap = BitmapFactory.decodeFile(imagePath, options);  
//            // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象  
//            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
//                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
//            return bitmap;  
//        }  
//      
//        /** 
//         * 获取视频的缩略图 
//         * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。 
//         * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。 
//         * @param videoPath 视频的路径 
//         * @param width 指定输出视频缩略图的宽度 
//         * @param height 指定输出视频缩略图的高度度 
//         * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 
//         *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96 
//         * @return 指定大小的视频缩略图 
//         */  
//        private Bitmap getVideoThumbnail(String videoPath, int width, int height,  
//                int kind) {  
//            Bitmap bitmap = null;  
//            // 获取视频的缩略图  
//            bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
//            System.out.println("w"+bitmap.getWidth());  
//            System.out.println("h"+bitmap.getHeight());  
//            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
//                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
//            return bitmap;  
//        }  
//          
//    }  
