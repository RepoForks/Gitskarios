package com.alorma.github.ui.activity.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.fragment.commit.CommitDetailBottomSheetFragment;
import com.alorma.github.ui.fragment.commit.CommitsListFragment;
import core.repositories.Commit;

public class BranchCommitsActivity extends RepositoryThemeActivity implements CommitsListFragment.CommitSelectedCallback {

  private static final String REPO_INFO = "REPO_INFO";
  private RepoInfo repoInfo;

  public static Intent createLauncherIntent(Context context, RepoInfo repoInfo) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(REPO_INFO, repoInfo);

    Intent intent = new Intent(context, BranchCommitsActivity.class);
    intent.putExtras(bundle);

    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar_responsive);

    if (getIntent() != null && getIntent().getExtras() != null) {
      repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);
      if (repoInfo != null) {
        setTitle(repoInfo.toString());

        if (getSupportActionBar() != null) {
          getSupportActionBar().setSubtitle(repoInfo.branch);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, CommitsListFragment.newInstance(repoInfo));
        ft.commit();
      }
    }
  }

  @Override
  public void onCommitSelected(Commit commit) {
    CommitDetailBottomSheetFragment fragment = CommitDetailBottomSheetFragment.newInstance(repoInfo, commit);
    fragment.show(getSupportFragmentManager(), "");
  }
}
