package lizec.lizec.tlock.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lizec.lizec.tlock.R;
import lizec.lizec.tlock.aes.database.AESMap;
import lizec.lizec.tlock.model.PwdInfo;

public class PwdAdapter extends RecyclerView.Adapter<PwdAdapter.ViewHolder> {
    private List<PwdInfo> mPwdInfoList;

    public PwdAdapter(AESMap map){
        int len = map.getAllKeys().size();
        mPwdInfoList = new ArrayList<>(len+3);

        for(String key: map.getAllKeys()){
            mPwdInfoList.add(map.get(key));
        }
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
        }
    }

    public void addItemAndNotify(PwdInfo newInfo){
        mPwdInfoList.add(newInfo);
        notifyItemInserted(mPwdInfoList.size()-1);
    }
}
