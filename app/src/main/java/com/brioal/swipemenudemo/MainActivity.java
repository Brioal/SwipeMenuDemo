package com.brioal.swipemenudemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brioal.swipemenu.view.SwipeMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Bind(R.id.main_swipemenu)
    SwipeMenu mMainSwipemenu;
    @Bind(R.id.menu_rg_trans)
    RadioGroup mRgTrans;
    @Bind(R.id.menu_cb_scale)
    CheckBox mCbScale;
    @Bind(R.id.menu_sb_scale)
    SeekBar mSbScale;
    @Bind(R.id.menu_cb_alpha)
    CheckBox mCbAlpha;
    @Bind(R.id.menu_sb_alpha)
    SeekBar mSbAlpha;
    @Bind(R.id.menu_rg_rotate)
    RadioGroup mRgRotate;
    @Bind(R.id.menu_Sb_rotate)
    SeekBar mSbRotate;
    @Bind(R.id.main_btn_menu)
    ImageButton mBtnMenu;
    @Bind(R.id.content_recyclerView)
    RecyclerView mContentRecyclerView;
    @Bind(R.id.cb_pic)
    CheckBox mCbPic;
    @Bind(R.id.content_rg_blur)
    RadioGroup mContentRgBlur;

    private int mStyleCode = 11111; //风格代码
    private int mScaleProgress = 0; //起始缩放程度
    private int mAlphaProgress = 0; //起始透明程度
    private int mAngleProgress = 0; //起始3D旋转角度

    private int mTransCode = 1; //移动动画代码
    private int mScaleCode = 1; //缩放动画代码
    private int mAlphaCode = 1; //透明度动画代码
    private int mRotateCode = 1; //旋转动画代码

    private List<String> mTips;
    private Context mContext;
    private TipAdapter mTipAdapter;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initTranslate();
        initScale();
        initAlpha();
        initRotate();
        initRecyclerView();
        mPreferences = getPreferences(Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        int isPic = mPreferences.getInt("Pic", 0);
        boolean isBlur = mPreferences.getBoolean("IsBlur", false);
        boolean isChangeBlur = mPreferences.getBoolean("IsChangeBlur", false);
        boolean isReverseChangeBlur = mPreferences.getBoolean("IsReverseChangeBlur", false);
        if (isPic == 0) {
            mMainSwipemenu.setFullColor(MainActivity.this, R.color.colorPrimary);
        } else {
            mCbPic.setChecked(true);
            if (isBlur) {
                mContentRgBlur.check(R.id.content_rb_blur);
                mMainSwipemenu.setBlur(MainActivity.this, R.mipmap.dayu, R.color.colorPrimary, 22f);
            } else if (isChangeBlur) {
                mContentRgBlur.check(R.id.content_rb_changeblur);
                mMainSwipemenu.setChangedBlur(MainActivity.this, R.mipmap.dayu, R.color.colorPrimary);
            } else if (isReverseChangeBlur) {
                mContentRgBlur.check(R.id.content_rb_reversechangeblur);
                mMainSwipemenu.setReverseChangedBlur(MainActivity.this, R.mipmap.dayu, R.color.colorPrimary);
            } else {

                mContentRgBlur.check(-1);
                mMainSwipemenu.setBackImage(MainActivity.this, R.mipmap.dayu, R.color.colorPrimary);
            }
        }
        mBtnMenu.setOnClickListener(this);
        mCbPic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mEditor = mPreferences.edit();
                if (b) {
                    mEditor.putInt("Pic", 1);
                } else {
                    mEditor.putInt("Pic", 0);
                }
                mEditor.commit();
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mContentRgBlur.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mEditor = mPreferences.edit();
                switch (i) {
                    case R.id.content_rb_blur:
                        mEditor.putBoolean("IsBlur", true);
                        mEditor.putBoolean("IsChangeBlur", false);
                        mEditor.putBoolean("IsReverseChangeBlur", false);
                        break;
                    case R.id.content_rb_changeblur:
                        mEditor.putBoolean("IsBlur", false);
                        mEditor.putBoolean("IsChangeBlur", true);
                        mEditor.putBoolean("IsReverseChangeBlur", true);
                        break;
                    case R.id.content_rb_reversechangeblur:
                        mEditor.putBoolean("IsBlur", false);
                        mEditor.putBoolean("IsChangeBlur", false);
                        mEditor.putBoolean("IsReverseChangeBlur", true);
                        break;
                }
                mEditor.putInt("Pic", 1);
                mEditor.commit();
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //初始化提示列表
    private void initRecyclerView() {
        if (mTipAdapter == null) {
            mTips = new ArrayList<>();
            mTips.add("本侧滑菜单库使用非常简单");
            mTips.add("提供多达144种动画效果");
            mTips.add("顶部切换按钮提供图片沉浸与颜色沉浸两种模式");
            mTips.add("并且支持动态模糊");
            mTips.add("模糊程度与范围可自定义");
            mTips.add("此用例主要用于效果预览");
            mTips.add("其次是显示参数设置方法");
            mTips.add("再次是用表明已处理滑动事件冲突");
            mTips.add("根据左边可视化调整的效果");
            mTips.add("只要按照顶部显示的参数设置即可");
            mTips.add("建议中心旋转搭配透明度动画,否则会有明显卡顿");
            mTipAdapter = new TipAdapter();
            mContentRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mContentRecyclerView.setAdapter(mTipAdapter);
        } else {
            mTipAdapter.notifyDataSetChanged();
        }
    }

    //初始化旋转动画
    private void initRotate() {
        mRgRotate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_rotate1:
                        mRotateCode = 1;
                        break;
                    case R.id.rb_rotate2:
                        mRotateCode = 2;
                        break;
                    case R.id.rb_rotate3:
                        mRotateCode = 3;
                        break;
                    case R.id.rb_rotate4:
                        mRotateCode = 4;
                        break;
                    case R.id.rb_rotate5:
                        mRotateCode = 5;
                        break;
                    case R.id.rb_rotate6:
                        mRotateCode = 6;
                        break;
                }

                changeStyleCode();
            }
        });
        mSbRotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mAngleProgress = i;
                mMainSwipemenu.setStart3DAngle((int) (i * 1.0f / 100 * 90));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //初始化透明动画
    private void initAlpha() {
        mCbAlpha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mAlphaCode = 2;
                } else {
                    mAlphaCode = 1;
                }
                changeStyleCode();
            }
        });
        mSbAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mAlphaProgress = i;
                mMainSwipemenu.setStartAlpha(i * 1.0f / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    //初始化缩放动画
    private void initScale() {
        mCbScale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mScaleCode = 2;
                } else {
                    mScaleCode = 1;
                }
                changeStyleCode();
            }
        });
        mSbScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mScaleProgress = i;
                mMainSwipemenu.setStartScale(i * 1.0f / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //初始化移动动画
    private void initTranslate() {
        mRgTrans.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_trans1:
                        mTransCode = 1;
                        break;
                    case R.id.rb_trans2:
                        mTransCode = 2;
                        break;
                    case R.id.rb_trans3:
                        mTransCode = 3;
                        break;
                }
                changeStyleCode();
            }
        });
    }

    //更新风格代码
    public void changeStyleCode() {
        mStyleCode = mTransCode * 1000 + mScaleCode * 100 + mAlphaCode * 10 + mRotateCode;
        Toast.makeText(mContext, mStyleCode + "", Toast.LENGTH_SHORT).show();
        mMainSwipemenu.setStyleCode(mStyleCode);
        initRecyclerView();
    }

    @Override
    public void onBackPressed() {
        if (mMainSwipemenu.isMenuShowing()) {
            mMainSwipemenu.hideMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_btn_menu:
                if (mMainSwipemenu.isMenuShowing()) {
                    mMainSwipemenu.hideMenu();
                } else {
                    mMainSwipemenu.showMenu();
                }
                break;
        }
    }

    class TipAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new TipViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_tip, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String context = "";
            if (position == 0) {
                StringBuilder builder = new StringBuilder();
                builder.append("setStyleCode(" + mStyleCode + ")\n");
                if (mCbPic.isChecked()) {
                    builder.append("setBackImage(MainActivity.this, R.mipmap.dayu, R.color.colorPrimary)\n");
                } else {
                    builder.append("setFullColor(MainActivity.this, R.color.colorPrimary\n");
                }
                if (mCbScale.isChecked()) { //存在缩放
                    builder.append("setMinScale(" + (mScaleProgress * 1.0f / 100) + ")\n");
                }
                if (mCbAlpha.isChecked()) { //存在透明度改变
                    builder.append("setStartAlpha(" + (mAlphaProgress * 1.0f / 100) + ")\n");
                }
                if (mRgRotate.getCheckedRadioButtonId() == R.id.rb_rotate3) { //存在旋转动态
                    builder.append("setStart3DAngle(" + (mAngleProgress * 1.0f / 100) + ")");
                }
                if (mCbPic.isChecked() && mContentRgBlur.getCheckedRadioButtonId() == -1) { //
                    builder.append("setBackImage(MainActivity.this, R.mipmap.dayu, R.color.colorPrimary)\n");
                }
                if (mContentRgBlur.getCheckedRadioButtonId() == R.id.content_rb_blur) { //静态模糊
                    builder.append("setBlur(MainActivity.this, R.mipmap.dayu, R.color.colorPrimary, 22f)\n");
                }
                if (mContentRgBlur.getCheckedRadioButtonId() == R.id.content_rb_changeblur) { //动态模糊
                    builder.append("setChangedBlur(MainActivity.this, R.mipmap.dayu, R.color.colorPrimary)\n");
                }
                if (mContentRgBlur.getCheckedRadioButtonId() == R.id.content_rb_changeblur) { //动态模糊
                    builder.append("setReverseChangedBlur(MainActivity.this, R.mipmap.dayu, R.color.colorPrimary)");
                }
                context = builder.toString();
            } else {
                context = mTips.get(position - 1);
            }
            ((TipViewHolder) holder).mTvContext.setText(context);
        }

        @Override
        public int getItemCount() {
            return mTips.size() + 1;
        }


    }


    //内容提示面板
    class TipViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tip_tv_content)
        TextView mTvContext;

        public TipViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
