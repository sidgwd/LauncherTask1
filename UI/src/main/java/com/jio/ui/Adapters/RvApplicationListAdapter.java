package com.jio.ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jio.sdk.DeviceApplicationUtility;
import com.jio.sdk.models.ApplicationModel;
import com.jio.ui.R;

import java.util.ArrayList;


public class RvApplicationListAdapter extends RecyclerView.Adapter {
    Context context;
    public ArrayList<ApplicationModel> appModel;

    public RvApplicationListAdapter(Context context,
                                    ArrayList<ApplicationModel> appModel) {
        this.context = context;
        this.appModel = appModel;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_list_row, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {

        MyHolder holder = (MyHolder) mainHolder;
        final ApplicationModel appModel = this.appModel.get(position);
        holder.tvAppName.setText(appModel.getAppName());
        Glide.with(context)
                .load(appModel.getAppIcon())
                .into(holder.imgvAppIcon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(context.getPackageManager().getLaunchIntentForPackage(appModel.getAppPackage()));
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DeviceApplicationUtility deviceApplicationUtility = DeviceApplicationUtility.getInstance();
                deviceApplicationUtility.showPopup(context, appModel.getAppName(), "Package Name :" + appModel.getAppPackage() + "\n" +
                        "Launcher Class :" + appModel.getLaunchActivity()
                        + "\n" + "App Version Name :" + appModel.getVersionName() + "\n" +
                        "App Version Code :" + appModel.getVersionCode());

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return appModel.size();
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        TextView tvAppName;
        ImageView imgvAppIcon;

        public MyHolder(View itemView) {
            super(itemView);
            imgvAppIcon = itemView.findViewById(R.id.imgvAppIcon);
            tvAppName = itemView.findViewById(R.id.tvAppName);
        }
    }

    public void filteredList(ArrayList<ApplicationModel> filterData) {
        this.appModel = filterData;
        notifyDataSetChanged();
    }
}
