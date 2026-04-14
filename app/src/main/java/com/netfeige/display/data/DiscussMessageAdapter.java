package com.netfeige.display.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.netfeige.R;
import com.netfeige.common.GifTextDrawable;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Public_Def;
import com.netfeige.common.Public_Tools;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DiscussMessageAdapter extends ArrayAdapter<MsgRecord> implements View.OnClickListener {
    public Html.ImageGetter imgGetter;
    private String mStrDiscussID;
    private IpmsgApplication myApp;

    public DiscussMessageAdapter(Context context, int i, List<MsgRecord> list, String str) {
        super(context, i, list);
        this.imgGetter = new Html.ImageGetter() { // from class: com.netfeige.display.data.DiscussMessageAdapter.1
            @Override // android.text.Html.ImageGetter
            public Drawable getDrawable(String str2) {
                try {
                    Drawable drawableCreateFromStream = Drawable.createFromStream(new URL(str2).openStream(), "");
                    drawableCreateFromStream.setBounds(0, 0, drawableCreateFromStream.getIntrinsicWidth(), drawableCreateFromStream.getIntrinsicHeight());
                    return drawableCreateFromStream;
                } catch (Exception unused) {
                    return null;
                }
            }
        };
        this.myApp = (IpmsgApplication) ((Activity) context).getApplication();
        this.mStrDiscussID = str;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;
        TextView textView2;
        LinearLayout linearLayout;
        ProgressBar progressBar;
        TextView textView3;
        TextView textView4;
        LinearLayout linearLayout2;
        ImageView imageView;
        TextView textView5;
        MsgRecord item = getItem(i);
        View viewInflate = view == null ? LayoutInflater.from(getContext()).inflate(R.layout.messageitem, (ViewGroup) null) : view;
        viewInflate.setTag(item);
        LinearLayout linearLayout3 = (LinearLayout) viewInflate.findViewById(R.id.content_llayout_messageitemto);
        LinearLayout linearLayout4 = (LinearLayout) viewInflate.findViewById(R.id.content_llayout_messageitemfrom);
        ((Button) viewInflate.findViewById(R.id.resend_btn_messageitemto)).setVisibility(8);
        LinearLayout linearLayout5 = (LinearLayout) viewInflate.findViewById(R.id.operate1_layout_filetranitemfrom);
        linearLayout5.setVisibility(8);
        Button button = (Button) viewInflate.findViewById(R.id.conreceive_btn_filetranitemfrom);
        Button button2 = (Button) viewInflate.findViewById(R.id.receive_btn_filetranitemfrom);
        Button button3 = (Button) viewInflate.findViewById(R.id.refuse_btn_filetranitemfrom);
        if (item.isSend()) {
            linearLayout4.setVisibility(8);
            linearLayout3.setVisibility(0);
            imageView = (ImageView) viewInflate.findViewById(R.id.file_image_filetranitemto);
            textView = (TextView) viewInflate.findViewById(R.id.name_text_messageitemto);
            textView5 = (TextView) viewInflate.findViewById(R.id.content_text_messageitemto);
            TextView textView6 = (TextView) viewInflate.findViewById(R.id.size_text_filetranitemto);
            linearLayout = (LinearLayout) viewInflate.findViewById(R.id.progress_layout_filetranitemto);
            progressBar = (ProgressBar) viewInflate.findViewById(R.id.progress_filetranitemto);
            textView3 = (TextView) viewInflate.findViewById(R.id.progress_text_filetranitemto);
            textView4 = (TextView) viewInflate.findViewById(R.id.date_text_filetranitemto);
            linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.operate_layout_filetranitemto);
            Button button4 = (Button) viewInflate.findViewById(R.id.cancel_btn_filetranitemto);
            button4.setVisibility(8);
            if (item.isFile()) {
                button4.setVisibility(0);
                button4.setTag(viewInflate);
                button4.setOnClickListener(this);
            }
            textView2 = textView6;
        } else {
            linearLayout4.setVisibility(0);
            linearLayout3.setVisibility(8);
            button.setVisibility(8);
            button2.setText(R.string.receive);
            ImageView imageView2 = (ImageView) viewInflate.findViewById(R.id.file_image_filetranitemfrom);
            textView = (TextView) viewInflate.findViewById(R.id.name_text_messageitemfrom);
            TextView textView7 = (TextView) viewInflate.findViewById(R.id.content_text_messageitemfrom);
            textView2 = (TextView) viewInflate.findViewById(R.id.size_text_filetranitemfrom);
            linearLayout = (LinearLayout) viewInflate.findViewById(R.id.progress_layout_filetranitemfrom);
            progressBar = (ProgressBar) viewInflate.findViewById(R.id.progress_filetranitemfrom);
            textView3 = (TextView) viewInflate.findViewById(R.id.progress_text_filetranitemfrom);
            textView4 = (TextView) viewInflate.findViewById(R.id.date_text_filetranitemfrom);
            linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.operate_layout_filetranitemfrom);
            Button button5 = (Button) viewInflate.findViewById(R.id.cancel_btn_filetranitemfrom);
            if (item.isFile()) {
                button.setTag(viewInflate);
                button.setOnClickListener(this);
                button2.setTag(viewInflate);
                button2.setOnClickListener(this);
                button3.setTag(viewInflate);
                button3.setOnClickListener(this);
                button5.setTag(viewInflate);
                button5.setOnClickListener(this);
            }
            imageView = imageView2;
            textView5 = textView7;
        }
        View view2 = viewInflate;
        HostInformation hostInfo = this.myApp.ipmsgService.getHostInfo(item.getFrom());
        if (hostInfo != null) {
            textView.setText(hostInfo.pszUserName);
        }
        imageView.setVisibility(8);
        textView2.setVisibility(8);
        linearLayout.setVisibility(8);
        linearLayout2.setVisibility(8);
        if (item.isFile()) {
            imageView.setVisibility(0);
            if (item.getnFileAttr() == 2) {
                imageView.setBackgroundResource(R.drawable.mfolder);
            } else {
                imageView.setBackgroundDrawable(this.myApp.getMIconByExtentionName(item.getFileName()));
            }
            textView2.setVisibility(0);
            textView2.setText(String.valueOf(Public_Tools.getShortSize(item.getSize())));
            if (item.getFileId() != -1) {
                linearLayout.setVisibility(0);
                linearLayout2.setVisibility(0);
                progressBar.setProgress(0);
                textView3.setText("");
                if (item.getFileInfo().fileTransMode == Public_Def.FileTransMode.FILETRANS_CONTINUE) {
                    button.setVisibility(0);
                    button2.setText(R.string.rereceive);
                }
                int i2 = AnonymousClass2.$SwitchMap$com$netfeige$common$Public_Def$TransStatus[item.getFileTranStatus().ordinal()];
                if (i2 == 1 || i2 == 2) {
                    linearLayout2.setVisibility(8);
                    linearLayout5.setVisibility(0);
                } else if (i2 == 3) {
                }
                progressBar.setProgress(item.getnProgress());
                textView3.setText(item.getStrCurrSpeed());
            }
        }
        List list = (List) textView5.getTag();
        if (list != null && list.size() > 0) {
            for (int i3 = 0; i3 < list.size(); i3++) {
                ((GifTextDrawable) list.get(i3)).stop();
            }
        }
        if (!item.isFailed()) {
            textView5.setClickable(true);
            textView5.setMovementMethod(LinkMovementMethod.getInstance());
            textView5.setText(((IpmsgApplication) ((Activity) getContext()).getApplication()).appendFace(item.bodyStr, textView5, item.screenShotList));
            if (((List) textView5.getTag()).size() > 0) {
                textView5.post(new GifRunnableImp((List) textView5.getTag()));
            }
        }
        textView4.setText(Html.fromHtml(item.getDate()));
        return view2;
    }

    /* JADX INFO: renamed from: com.netfeige.display.data.DiscussMessageAdapter$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$netfeige$common$Public_Def$TransStatus;

        static {
            int[] iArr = new int[Public_Def.TransStatus.values().length];
            $SwitchMap$com$netfeige$common$Public_Def$TransStatus = iArr;
            try {
                iArr[Public_Def.TransStatus.Trans_Rename.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$netfeige$common$Public_Def$TransStatus[Public_Def.TransStatus.Trans_Recving.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$netfeige$common$Public_Def$TransStatus[Public_Def.TransStatus.Trans_Sending.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:14:0x00a7  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0138  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0187  */
    @Override // android.view.View.OnClickListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onClick(android.view.View r13) {
        /*
            Method dump skipped, instruction units count: 496
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.netfeige.display.data.DiscussMessageAdapter.onClick(android.view.View):void");
    }

    public class GifRunnableImp implements Runnable {
        private List<GifTextDrawable> mImgList;

        public GifRunnableImp(List<GifTextDrawable> list) {
            this.mImgList = new ArrayList();
            this.mImgList = list;
        }

        @Override // java.lang.Runnable
        public void run() {
            for (int i = 0; i < this.mImgList.size(); i++) {
                this.mImgList.get(i).start();
            }
        }
    }
}

