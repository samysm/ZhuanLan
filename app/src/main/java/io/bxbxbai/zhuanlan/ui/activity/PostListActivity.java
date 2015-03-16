package io.bxbxbai.zhuanlan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import com.android.volley.Response;
import com.balysv.materialmenu.MaterialMenuDrawable;
import io.bxbxbai.zhuanlan.R;
import io.bxbxbai.zhuanlan.adapter.PostListAdapter;
import io.bxbxbai.zhuanlan.bean.Post;
import io.bxbxbai.zhuanlan.data.GsonRequest;
import io.bxbxbai.zhuanlan.data.RequestManager;
import io.bxbxbai.zhuanlan.utils.EndlessScrollListener;
import io.bxbxbai.zhuanlan.utils.ToastUtils;
import io.bxbxbai.zhuanlan.utils.ZhuanLanApi;
import io.bxbxbai.zhuanlan.view.circularprogress.CircularLoadingView;

import java.util.List;

/**
 *
 * @author bxbxbai
 */
public class PostListActivity extends BaseActivity {

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    public static final String KEY_DATA = "data";

    private ListView listView;

    private String id = "limiao";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        initToolBar();
        materialMenu.setState(MaterialMenuDrawable.IconState.ARROW);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = ButterKnife.findById(this, R.id.lv_post);
        final CircularLoadingView view = ButterKnife.findById(this ,R.id.v_loading);

        final PostListAdapter adapter = new PostListAdapter(this, null);
        listView.setAdapter(adapter);

        final Response.Listener listener = new Response.Listener<List<Post>>() {
            @Override
            public void onResponse(List<Post> response) {
                if (response.size() == 0) {
                    ToastUtils.showShort("没有数据了");
                }
                listView.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                adapter.addAll(response);
            }
        };

        id = getIntent().getStringExtra(KEY_ID);
        String name = getIntent().getStringExtra(KEY_NAME);

        if (!TextUtils.isEmpty(name)) {
            getSupportActionBar().setTitle(name);
        }


        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                GsonRequest<List<Post>> request = ZhuanLanApi.getPostListRequest(id,
                        String.valueOf((page - 1) * ZhuanLanApi.COUNT));
                request.setSuccessListener(listener);
                RequestManager.addRequest(request, id);
            }
        });

        GsonRequest<List<Post>> request = ZhuanLanApi.getPostListRequest(id, "0");
        request.setSuccessListener(listener);
        RequestManager.addRequest(request, id);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = (Post) view.getTag(R.id.key_data);
                WebActivity.startActivity(PostListActivity.this, post);
            }
        });

    }

    public static boolean start(Context context, String id) {
        return start(context, id, null);
    }

    public static boolean start(Context context, String id, String name) {
        Intent intent = new Intent();
        intent.setClass(context, PostListActivity.class);
        intent.putExtra(KEY_ID, id);
        intent.putExtra(KEY_NAME, name);
        context.startActivity(intent);
        return true;
    }
}
