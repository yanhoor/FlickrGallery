package com.example.yanhoor.flickrgallery;

import android.support.v4.app.Fragment;

/**
 * Created by yanhoor on 2016/3/30.
 */
public class TopicReplyActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String groupId=getIntent().getStringExtra(TopicReplyFragment.EXTRA_GROUP_ID);
        String topicId=getIntent().getStringExtra(TopicReplyFragment.EXTRA_TOPIC_ID);

        return TopicReplyFragment.newInstance(groupId,topicId);
    }

}
