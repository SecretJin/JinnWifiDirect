package com.example.acer.jinwifidirect.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.app.AlertDialog;

import com.example.acer.jinwifidirect.R;
import com.example.acer.jinwifidirect.repository.ProfileUtil;

import java.util.ArrayList;

public class ProfileListAdapter extends BaseAdapter implements ListAdapter
{
    private ArrayList<ProfileUtil> list = new ArrayList<ProfileUtil>();
    private Context context;
    private ProfileUtil profile;
    private AlertDialog alert;

    public ProfileListAdapter(ArrayList<ProfileUtil> list, Context context) {
        this.list = list;
        this.context = context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.profile_list, null);
        }

        list = ProfileUtil.getInstance().getList();
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        profile = list.get(position);
        listItemText.setText(profile.getName());

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button viewBtn=(Button)view.findViewById(R.id.view_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                list.remove(position);
                notifyDataSetChanged();
            }
        });


        viewBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, ProfileDetailsActivity.class);
                Object object = list.get(position);
                ProfileUtil profile = (ProfileUtil ) object;
                intent.putExtra("MyClass", profile);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
