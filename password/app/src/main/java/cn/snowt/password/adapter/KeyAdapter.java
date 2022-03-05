package cn.snowt.password.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.snowt.password.R;
import cn.snowt.password.activity.DetailActivity;
import cn.snowt.password.entity.Key;
import cn.snowt.password.service.KeyService;
import cn.snowt.password.service.impl.KeyServiceImpl;
import cn.snowt.password.util.BaseUtils;
import cn.snowt.password.util.SimpleResult;

/**
 * @Author: HibaraAi
 * @Date: 2022-03-05 09:49:34
 * @Description:
 */
public class KeyAdapter extends RecyclerView.Adapter{
    private List<Key> keyList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View keyView;
        TextView keyName;
        ImageView delImg;
        Integer keyId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            keyName = itemView.findViewById(R.id.keyitem_key_name);
            delImg = itemView.findViewById(R.id.keyitem_btn_del);
            keyView = itemView;
        }
    }

    public KeyAdapter(List<Key> keyList) {
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.key_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //在这里设置点击事件
        viewHolder.delImg.setOnClickListener(v->{
            int position = viewHolder.getAdapterPosition();
            Key key = keyList.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
            builder.setTitle("警告!");
            builder.setMessage("你确定要删除["+key.getName()+"] 吗?");
            builder.setCancelable(false);
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    KeyService keyService = new KeyServiceImpl();
                    SimpleResult result = keyService.deleteById(key.getId());
                    if(result.getSuccess()){
                        BaseUtils.shortTipInCoast(parent.getContext(),"["+key.getName()+"]已被删除");
                        keyList.remove(key);
                        notifyItemRangeRemoved(position,1);
                    }else{
                        BaseUtils.shortTipInCoast(parent.getContext(),result.getMsg());
                    }
                }
            });
            builder.show();
        });
        viewHolder.itemView.setOnClickListener(v->{
            int position = viewHolder.getAdapterPosition();
            Key key = keyList.get(position);
            Intent intent = new Intent(parent.getContext(), DetailActivity.class);
            intent.putExtra("keyId",key.getId());
            parent.getContext().startActivity(intent);
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder newHolder = (ViewHolder)holder;
        Key key = keyList.get(position);
        newHolder.keyName.setText(key.getName());
        newHolder.keyId = key.getId();
        holder = newHolder;
    }

    @Override
    public int getItemCount() {
        return keyList.size();
    }
}
