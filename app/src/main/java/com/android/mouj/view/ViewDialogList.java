package com.android.mouj.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.mouj.R;
import com.android.mouj.external.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.android.mouj.external.nostra13.universalimageloader.core.DisplayImageOptions;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoader;
import com.android.mouj.external.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.android.mouj.external.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;

/**
 * Created by ekobudiarto on 12/17/15.
 */
public class ViewDialogList extends Dialog {

    Context mContext;
    ViewText view_title;
    ListView listdata;
    ArrayList<String> data_id, data_title, data_thumb, data_desc;
    ViewButton button_cancel, button_ok;
    boolean isHiddenThumb = true, isHiddenDesc = true, isHiddenBtnOk = false,isHiddenBtnCancel = false, isHiddenTitle, choiceType = false;
    DisplayImageOptions opt;
    ImageLoader loader;
    DialogListAdapter adapter;
    String[] data_selected;
    String title, selected_index;


    public ViewDialogList(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public ViewDialogList(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        init();
    }

    protected ViewDialogList(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.base_dialog_list);

        listdata = (ListView) findViewById(R.id.base_dialog_listview);
        view_title = (ViewText) findViewById(R.id.base_dialog_title);
        button_cancel = (ViewButton) findViewById(R.id.base_dialog_cancel);
        button_ok = (ViewButton) findViewById(R.id.base_dialog_ok);
        view_title.setSemiBold();
        view_title.setText(title);
        if(isHiddenBtnOk)
        {
            button_ok.setVisibility(View.GONE);
        }
        if(isHiddenBtnCancel)
        {
            button_cancel.setVisibility(View.GONE);
        }
        if(isHiddenTitle)
        {
            view_title.setVisibility(View.GONE);
        }
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    private void init()
    {
        data_id = new ArrayList<String>();
        data_title = new ArrayList<String>();
        data_thumb = new ArrayList<String>();
        data_desc = new ArrayList<String>();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .threadPoolSize(5)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);


        opt = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        loader = ImageLoader.getInstance();
    }

    public void setDataID(ArrayList<String> dataID)
    {
        this.data_id = dataID;
    }

    public void setDataTitle(ArrayList<String> dataTitle)
    {
        this.data_title = dataTitle;
    }

    public void setDataThumb(ArrayList<String> dataThumb)
    {
        this.data_thumb = dataThumb;
    }

    public void setDataDesc(ArrayList<String> dataDesc)
    {
        this.data_desc = dataDesc;
    }

    public void setHiddenThumb(boolean isHiddenThumb)
    {
        this.isHiddenThumb = isHiddenThumb;
    }

    public void setHiddenDesc(boolean isHiddenDesc)
    {
        this.isHiddenDesc = isHiddenDesc;
    }

    public void setHiddenButtonOK(boolean hide)
    {
        this.isHiddenBtnOk = hide;
    }

    public void setHiddenButtonCancel(boolean hide)
    {
        this.isHiddenBtnCancel = hide;
    }

    public void setHiddenTitle(boolean hide)
    {
        this.isHiddenTitle = hide;
    }

    public void setChoiceType(boolean isChoice)
    {
        this.choiceType = isChoice;
    }

    public void setSelectedDataDefault(String data)
    {
        this.selected_index = data;
    }


    public ViewButton getButtonOK()
    {
        return this.button_ok;
    }

    public ViewButton getButtonCancel()
    {
        return this.button_cancel;
    }

    public ListView getListViewData()
    {
        return this.listdata;
    }



    public void executeList()
    {
        adapter = new DialogListAdapter();
        listdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                data_selected = (String[]) listdata.getAdapter().getItem(position);
            }
        });
        if(adapter != null)
        {
            if(data_id.size() > 0)
            {
                if(data_id.size() > 5)
                {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int) mContext.getResources().getDimension(R.dimen.max_height_dialog_listview));
                    listdata.setLayoutParams(params);
                }
                listdata.setAdapter(adapter);
            }
        }
    }

    public String[] getSelectedObject()
    {
        return this.data_selected;
    }




    public class DialogListAdapter extends BaseAdapter {

        private LayoutInflater inflater;



        public DialogListAdapter() {
            // TODO Auto-generated constructor stub
            this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder{
            ViewText base_title;
            ImageView base_thumb;
            ViewText base_desc;
            ImageView base_radio;

        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method s tub
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.base_dialog_list_item, null);
            }
            holder = new ViewHolder();
            holder.base_title = (ViewText) convertView.findViewById(R.id.base_dialog_listview_item_title);
            holder.base_thumb = (ImageView) convertView.findViewById(R.id.base_dialog_listview_item_thumb);
            holder.base_desc = (ViewText) convertView.findViewById(R.id.base_dialog_listview_item_desc);
            holder.base_radio = (ImageView) convertView.findViewById(R.id.base_dialog_radio_icon);


            holder.base_title.setSemiBold();
            holder.base_title.setText(data_title.get(position));
            if(selected_index != null && selected_index.equals(data_id.get(position)))
            {
                holder.base_radio.setPressed(true);
                holder.base_title.setTextColor(mContext.getResources().getColor(R.color.base_green));
            }
            if(choiceType)
            {
                holder.base_radio.setVisibility(View.VISIBLE);
            }

            if(isHiddenDesc && !isHiddenThumb)
            {
                holder.base_desc.setVisibility(View.GONE);
                holder.base_thumb.setVisibility(View.VISIBLE);
                loader.displayImage(data_thumb.get(position),holder.base_thumb,opt);
            }
            else if(!isHiddenDesc && isHiddenThumb)
            {
                holder.base_thumb.setVisibility(View.GONE);
                holder.base_desc.setVisibility(View.VISIBLE);
                holder.base_desc.setText(data_desc.get(position));
            }
            else if(!isHiddenDesc && !isHiddenThumb)
            {
                holder.base_thumb.setVisibility(View.VISIBLE);
                holder.base_desc.setVisibility(View.VISIBLE);
                holder.base_desc.setText(data_desc.get(position));
                loader.displayImage(data_thumb.get(position), holder.base_thumb, opt);
            }
            else
            {
                holder.base_thumb.setVisibility(View.GONE);
                holder.base_desc.setVisibility(View.GONE);
            }

            convertView.setTag(holder);

            return convertView;
        }

        public void clear()
        {
            data_id.clear();
            data_title.clear();
            data_thumb.clear();
            data_desc.clear();
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data_id.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            String[] object = new String[4];
            object[0] = data_id.get(arg0);
            object[1] = data_title.get(arg0);
            if(isHiddenDesc && !isHiddenThumb){
                object[2] = data_thumb.get(arg0);
                object[3] = "";
            }
            else if(!isHiddenDesc && isHiddenThumb)
            {
                object[2] = "";
                object[3] = data_desc.get(arg0);
            }
            else if(!isHiddenDesc && !isHiddenThumb)
            {
                object[2] = data_thumb.get(arg0);
                object[3] = data_thumb.get(arg0);
            }
            else {
                object[2] = "";
                object[3] = "";
            }

            return object;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }


}
