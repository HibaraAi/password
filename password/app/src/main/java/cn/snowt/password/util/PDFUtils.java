package cn.snowt.password.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import cn.snowt.password.R;
import cn.snowt.password.entity.Key;

/**
 * @Author: HibaraAi
 * @Date: 2024-11-30 10:05
 * @Description: PDFUtils
 */
public class PDFUtils {

    /**
     * 将List<Key> keys的数据导出到PDF中，每一个Key保存为一张paper，样式使用的是（R.layout.activity_detail），
     * 宽度为手机屏幕宽度，高度自适应。所有paper导出到一个PDF文件。
     * @param keys keys
     * @param context context
     * @param parent 随便一个ViewGroup，可以是最外层的layout
     */
    public static void createPdf(List<Key> keys, Context context, ViewGroup parent){
        PdfDocument document = new PdfDocument();
        PdfDocument.Page page = null;
        for(int i =0;i<keys.size();i++){
            Key key = keys.get(i);
            View view = voToView(key, context, parent);
            view.measure(View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap bitmap = viewToBitMap2(view);
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(parent.getWidth(), view.getMeasuredHeight(), i).create();
            // start a page
            page = document.startPage(pageInfo);
            if (page == null) {
                return;
            }
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(bitmap, 0, 0, null);
            document.finishPage(page);

        }
        // 1.write the document content
        String pdfDir = Environment.getExternalStoragePublicDirectory(Constant.EXTERNAL_STORAGE_LOCATION).getAbsolutePath()+"/PDF/";
        File file = new File(pdfDir);
        if(!file.exists()){
            file.mkdirs();
        }
        String s = file.getAbsolutePath()+"/"+ BaseUtils.dateToString(new Date()).substring(0,10)+"-"+ UUID.randomUUID().toString().substring(0,6) + ".pdf";
        try {
            // 1.1write the document content
            document.writeTo(new FileOutputStream(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // close the document
        document.close();
    }

    /**
     * 将View转成Bitmap
     * @param view view
     * @return Bitmap
     */
    private static Bitmap viewToBitMap2(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }


    /**
     * 把Key实体转换成View（R.layout.activity_detail）
     * @param key key
     * @param context context
     * @param parent 随便一个ViewGroup，可以是最外层的layout
     * @return View（R.layout.activity_detail）
     */
    private static View voToView(Key key, Context context, ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.key_item_pdf, parent, false);
        //viewHolder与view文件绑定
        TextView keyName = view.findViewById(R.id.detail_value_name);
        TextView account1 = view.findViewById(R.id.detail_value_account1);
        TextView password1 = view.findViewById(R.id.detail_value_password1);
        TextView account2 = view.findViewById(R.id.detail_value_account2);
        TextView password2 = view.findViewById(R.id.detail_value_password2);
        TextView otherName = view.findViewById(R.id.detail_value_otherName);
        TextView remark = view.findViewById(R.id.detail_value_remark);
        TextView date = view.findViewById(R.id.detail_value_date);
        //为viewHolder填写数据
        keyName.setText(key.getName());
        account1.setText(key.getAccountOne());
        password1.setText(key.getPasswordOne());
        account2.setText(key.getAccountTwo());
        password2.setText(key.getPasswordTwo());
        otherName.setText(key.getOtherName());
        remark.setText(key.getRemarks());
        date.setText(BaseUtils.dateToString(key.getCreateDate()));
        return view;
    }

}
