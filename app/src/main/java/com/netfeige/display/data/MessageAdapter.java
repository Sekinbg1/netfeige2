package com.netfeige.display.data;

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
import com.netfeige.display.ui.ChatActivity;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MessageAdapter extends ArrayAdapter<MsgRecord> implements View.OnClickListener {
    private ChatActivity chatActivity;
    public Html.ImageGetter imgGetter;
    private IpmsgApplication myApp;

    public MessageAdapter(Context context, int i, List<MsgRecord> list) {
        super(context, i, list);
        this.imgGetter = new Html.ImageGetter() { // from class: com.netfeige.display.data.MessageAdapter.2
            @Override // android.text.Html.ImageGetter
            public Drawable getDrawable(String str) {
                try {
                    Drawable drawableCreateFromStream = Drawable.createFromStream(new URL(str).openStream(), "");
                    drawableCreateFromStream.setBounds(0, 0, drawableCreateFromStream.getIntrinsicWidth(), drawableCreateFromStream.getIntrinsicHeight());
                    return drawableCreateFromStream;
                } catch (Exception unused) {
                    return null;
                }
            }
        };
        ChatActivity chatActivity = (ChatActivity) context;
        this.myApp = (IpmsgApplication) chatActivity.getApplication();
        this.chatActivity = chatActivity;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;
        LinearLayout linearLayout;
        View view2;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        ProgressBar progressBar;
        TextView textView5;
        LinearLayout linearLayout2;
        int i2;
        Button button;
        ImageView imageView;
        MsgRecord item = getItem(i);
        View viewInflate = view == null ? LayoutInflater.from(getContext()).inflate(R.layout.messageitem, (ViewGroup) null) : view;
        viewInflate.setTag(item);
        LinearLayout linearLayout3 = (LinearLayout) viewInflate.findViewById(R.id.content_llayout_messageitemto);
        LinearLayout linearLayout4 = (LinearLayout) viewInflate.findViewById(R.id.content_llayout_messageitemfrom);
        Button button2 = (Button) viewInflate.findViewById(R.id.resend_btn_messageitemto);
        button2.setVisibility(8);
        LinearLayout linearLayout5 = (LinearLayout) viewInflate.findViewById(R.id.operate1_layout_filetranitemfrom);
        linearLayout5.setVisibility(8);
        Button button3 = (Button) viewInflate.findViewById(R.id.conreceive_btn_filetranitemfrom);
        Button button4 = (Button) viewInflate.findViewById(R.id.receive_btn_filetranitemfrom);
        Button button5 = (Button) viewInflate.findViewById(R.id.refuse_btn_filetranitemfrom);
        if (item.isSend()) {
            linearLayout4.setVisibility(8);
            linearLayout3.setVisibility(0);
            ImageView imageView2 = (ImageView) viewInflate.findViewById(R.id.file_image_filetranitemto);
            TextView textView6 = (TextView) viewInflate.findViewById(R.id.name_text_messageitemto);
            TextView textView7 = (TextView) viewInflate.findViewById(R.id.content_text_messageitemto);
            TextView textView8 = (TextView) viewInflate.findViewById(R.id.size_text_filetranitemto);
            LinearLayout linearLayout6 = (LinearLayout) viewInflate.findViewById(R.id.progress_layout_filetranitemto);
            ProgressBar progressBar2 = (ProgressBar) viewInflate.findViewById(R.id.progress_filetranitemto);
            TextView textView9 = (TextView) viewInflate.findViewById(R.id.progress_text_filetranitemto);
            TextView textView10 = (TextView) viewInflate.findViewById(R.id.date_text_filetranitemto);
            LinearLayout linearLayout7 = (LinearLayout) viewInflate.findViewById(R.id.operate_layout_filetranitemto);
            Button button6 = (Button) viewInflate.findViewById(R.id.cancel_btn_filetranitemto);
            button6.setVisibility(8);
            if (item.isFile()) {
                button6.setVisibility(0);
                button6.setTag(viewInflate);
                button6.setOnClickListener(this);
            }
            textView5 = textView6;
            textView = textView10;
            button = button6;
            textView2 = textView8;
            imageView = imageView2;
            textView4 = textView7;
            linearLayout = linearLayout7;
            view2 = viewInflate;
            textView3 = textView9;
            progressBar = progressBar2;
            linearLayout2 = linearLayout6;
            i2 = 8;
        } else {
            linearLayout4.setVisibility(0);
            linearLayout3.setVisibility(8);
            button3.setVisibility(8);
            button4.setText(R.string.receive);
            ImageView imageView3 = (ImageView) viewInflate.findViewById(R.id.file_image_filetranitemfrom);
            TextView textView11 = (TextView) viewInflate.findViewById(R.id.name_text_messageitemfrom);
            TextView textView12 = (TextView) viewInflate.findViewById(R.id.content_text_messageitemfrom);
            TextView textView13 = (TextView) viewInflate.findViewById(R.id.size_text_filetranitemfrom);
            LinearLayout linearLayout8 = (LinearLayout) viewInflate.findViewById(R.id.progress_layout_filetranitemfrom);
            ProgressBar progressBar3 = (ProgressBar) viewInflate.findViewById(R.id.progress_filetranitemfrom);
            TextView textView14 = (TextView) viewInflate.findViewById(R.id.progress_text_filetranitemfrom);
            TextView textView15 = (TextView) viewInflate.findViewById(R.id.date_text_filetranitemfrom);
            LinearLayout linearLayout9 = (LinearLayout) viewInflate.findViewById(R.id.operate_layout_filetranitemfrom);
            Button button7 = (Button) viewInflate.findViewById(R.id.cancel_btn_filetranitemfrom);
            if (item.isFile()) {
                button3.setTag(viewInflate);
                button3.setOnClickListener(this);
                button4.setTag(viewInflate);
                button4.setOnClickListener(this);
                button5.setTag(viewInflate);
                button5.setOnClickListener(this);
                button7.setTag(viewInflate);
                button7.setOnClickListener(this);
            }
            textView = textView15;
            linearLayout = linearLayout9;
            view2 = viewInflate;
            textView2 = textView13;
            textView3 = textView14;
            textView4 = textView12;
            progressBar = progressBar3;
            textView5 = textView11;
            linearLayout2 = linearLayout8;
            i2 = 8;
            button = button7;
            imageView = imageView3;
        }
        textView5.setVisibility(i2);
        imageView.setVisibility(i2);
        textView2.setVisibility(i2);
        linearLayout2.setVisibility(i2);
        linearLayout.setVisibility(i2);
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
                linearLayout2.setVisibility(0);
                linearLayout.setVisibility(0);
                progressBar.setProgress(0);
                textView3.setText("");
                if (item.getFileInfo().fileTransMode == Public_Def.FileTransMode.FILETRANS_CONTINUE) {
                    button3.setVisibility(0);
                    button4.setText(R.string.rereceive);
                }
                int i3 = AnonymousClass3.$SwitchMap$com$netfeige$common$Public_Def$TransStatus[item.getFileTranStatus().ordinal()];
                if (i3 == 1 || i3 == 2) {
                    linearLayout.setVisibility(8);
                    linearLayout5.setVisibility(0);
                } else if (i3 == 3) {
                }
                progressBar.setProgress(item.getnProgress());
                textView3.setText(item.getStrCurrSpeed());
            }
        }
        List list = (List) textView4.getTag();
        if (list != null && list.size() > 0) {
            for (int i4 = 0; i4 < list.size(); i4++) {
                ((GifTextDrawable) list.get(i4)).stop();
            }
        }
        if (item.isFailed()) {
            linearLayout.setVisibility(0);
            button.setVisibility(8);
            button2.setVisibility(0);
            button2.setTag(item);
            button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.data.MessageAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    HostInformation hostInfo = MessageAdapter.this.chatActivity.getHostInfo();
                    if (hostInfo == null) {
                        Public_Tools.showToast(MessageAdapter.this.chatActivity, MessageAdapter.this.chatActivity.getString(R.string.offline_prompt), 1);
                        return;
                    }
                    MsgRecord msgRecord = (MsgRecord) view3.getTag();
                    long jSendMsg = MessageAdapter.this.myApp.ipmsgService.m_DataSource.m_Protocol.sendMsg(msgRecord.bodyStr, hostInfo);
                    msgRecord.setFailed(false);
                    msgRecord.setnPackageID(jSendMsg);
                    MessageAdapter.this.notifyDataSetChanged();
                }
            });
            textView4.setText(((IpmsgApplication) ((ChatActivity) getContext()).getApplication()).appendPrompt(item.bodyStr, textView4, item.screenShotList));
        } else {
            textView4.setClickable(true);
            textView4.setMovementMethod(LinkMovementMethod.getInstance());
            textView4.setText(((IpmsgApplication) ((ChatActivity) getContext()).getApplication()).appendFace(item.bodyStr, textView4, item.screenShotList));
            if (((List) textView4.getTag()).size() > 0) {
                textView4.post(new GifRunnableImp((List) textView4.getTag()));
            }
        }
        textView.setText(Html.fromHtml(item.getDate()));
        return view2;
    }

    /* JADX INFO: renamed from: com.netfeige.display.data.MessageAdapter$3, reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
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
    /* JADX WARN: Removed duplicated region for block: B:17:0x00ac  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0137  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0186  */
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
        throw new UnsupportedOperationException("Method not decompiled: com.netfeige.display.data.MessageAdapter.onClick(android.view.View):void");
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

