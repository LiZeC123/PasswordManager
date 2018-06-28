package lizec.lizec.tlock.base;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lizec.lizec.tlock.R;
import lizec.lizec.tlock.aes.database.AESMap;
import lizec.lizec.tlock.model.PwdInfo;

public class PwdAdapter extends RecyclerView.Adapter<PwdAdapter.ViewHolder> {
    private List<PwdInfo> mPwdInfoList;
    private static WeakReference<Context> contextWeakReference;
    public PwdAdapter(AESMap map, Context context){
        int len = map.getAllKeys().size();
        mPwdInfoList = new ArrayList<>(len+3);

        for(String key: map.getAllKeys()){
            mPwdInfoList.add(map.get(key));
        }

        contextWeakReference = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public PwdAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_pwd_show,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PwdAdapter.ViewHolder holder, int position) {
        PwdInfo info = mPwdInfoList.get(position);
        holder.txtAPP.setText(info.getAPPName());
        holder.txtName.setText(info.getUserName());
        holder.txtPwd.setText(info.getPwd());

    }

    @Override
    public int getItemCount() {
        return mPwdInfoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtAPP, txtName,txtPwd;

        ViewHolder(View itemView) {
            super(itemView);
            txtAPP = itemView.findViewById(R.id.txtAPP);
            txtName = itemView.findViewById(R.id.txtName);
            txtPwd = itemView.findViewById(R.id.txtPwd);
            itemView.findViewById(R.id.btnUserCopy).setOnClickListener(v->{
                Context context = contextWeakReference.get();
                ClipboardManager manager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("",txtName.getText());
                Objects.requireNonNull(manager).setPrimaryClip(mClipData);
                Toast.makeText(context,"用户名已复制",Toast.LENGTH_SHORT).show();
            });

            itemView.findViewById(R.id.btnPwdCopy).setOnClickListener(v->{
                Context context = contextWeakReference.get();
                ClipboardManager manager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("",txtPwd.getText());
                Objects.requireNonNull(manager).setPrimaryClip(mClipData);
                Toast.makeText(context,"密码已复制",Toast.LENGTH_SHORT).show();
            });
        }
    }

    public void addItemAndNotify(PwdInfo newInfo){
        mPwdInfoList.add(newInfo);
        notifyItemInserted(mPwdInfoList.size()-1);
    }
}
