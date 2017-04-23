package com.molmc.intoyundemo.support.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.marshalchen.ultimaterecyclerview.ui.timelineview.TimelineView;
import com.molmc.intoyunsdk.bean.MessageBean;
import com.molmc.intoyunsdk.network.IntoYunListener;
import com.molmc.intoyunsdk.network.NetError;
import com.molmc.intoyunsdk.openapi.IntoYunSdk;
import com.molmc.intoyundemo.R;
import com.molmc.intoyundemo.utils.DialogUtil;
import com.molmc.intoyundemo.utils.Interface;
import com.molmc.intoyundemo.utils.Utils;

import java.util.List;

/**
 * Created by zJJ on 4/27/2016.
 */
public class MessageAdapter extends easyRegularAdapter<MessageBean, MessageAdapter.MessageNode> {

    private Context mContext;

    public MessageAdapter(Context context, List<MessageBean> feedList) {
        super(feedList);
        this.mContext = context;
    }

    public void setData(List<MessageBean> messages) {
        source = messages;
        notifyDataSetChanged();
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.message_node;
    }

    @Override
    protected MessageNode newViewHolder(View view) {
        return new MessageNode(view);
    }

    @Override
    protected void withBindHolder(MessageNode holder, MessageBean message, int position) {
        holder.itemView.setOnLongClickListener(onLongClickListener(position, message));
        holder.name.setText(Utils.timestampToString(message.getTimestamp()) + "\n" + message.getContent());
        holder.init(TimelineView.getTimeLineViewType(position, getItemCount()));
    }

    private View.OnLongClickListener onLongClickListener(final int position, final MessageBean message) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogUtil.showDialog(mContext, R.string.delete_tips, R.string.delete_content, R.string.cancel, R.string.confirm, new Interface.DialogCallback() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        IntoYunSdk.deleteMessageById(message.get_id(), new IntoYunListener() {
                            @Override
                            public void onSuccess(Object result) {
                                removeAt(position);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFail(NetError error) {
                                DialogUtil.showToast(error.getMessage());
                            }
                        });
                    }
                });
                return true;
            }
        };
    }

    static class MessageNode extends UltimateRecyclerviewViewHolder {
        public TimelineView mTimelineView;
        public TextView name;

        /**
         * the view
         *
         * @param itemView the view context
         */
        public MessageNode(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tx_name);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        }

        /**
         * this is the initialization of the node
         *
         * @param viewTypeLine the type of node to redraw
         */
        public void init(int viewTypeLine) {
            mTimelineView.initLine(viewTypeLine);
        }
    }
}
