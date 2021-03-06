package com.alorma.github.ui.fragment.orgs;

import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import com.alorma.github.R;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.orgs.GetOrgsClient;
import com.alorma.github.ui.adapter.orgs.OrganizationsAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.octicons_typeface_library.Octicons;
import core.User;
import java.util.List;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class OrganizationsFragment extends LoadingListFragment<OrganizationsAdapter> implements Observer<List<User>> {
  private String username;

  public static OrganizationsFragment newInstance() {
    return new OrganizationsFragment();
  }

  public static OrganizationsFragment newInstance(String username) {
    OrganizationsFragment followersFragment = new OrganizationsFragment();
    if (username != null) {
      Bundle bundle = new Bundle();
      bundle.putString(USERNAME, username);

      followersFragment.setArguments(bundle);
    }
    return followersFragment;
  }

  @StyleRes
  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_People;
  }

  @StyleRes
  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_People;
  }

  @Override
  protected void executeRequest() {
    super.executeRequest();
    setAction(new GetOrgsClient(username));
  }

  @Override
  protected void executePaginatedRequest(int page) {
    super.executePaginatedRequest(page);
    setAction(new GetOrgsClient(username, page));
  }

  private void setAction(GithubListClient<List<User>> getOrgsClient) {
    startRefresh();
    getOrgsClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(new Func1<Pair<List<User>, Integer>, List<User>>() {
          @Override
          public List<User> call(Pair<List<User>, Integer> listIntegerPair) {
            setPage(listIntegerPair.second);
            return listIntegerPair.first;
          }
        })
        .subscribe(this);
  }

  @Override
  public void onCompleted() {
    stopRefresh();
  }

  @Override
  public void onError(Throwable e) {

  }

  @Override
  public void onNext(List<User> organizations) {
    if (organizations.size() > 0) {
      hideEmpty();
      if (refreshing || getAdapter() == null) {
        OrganizationsAdapter adapter = new OrganizationsAdapter(LayoutInflater.from(getActivity()));
        adapter.addAll(organizations);
        setAdapter(adapter);
      } else {
        getAdapter().addAll(organizations);
      }
    } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
      setEmpty();
    } else {
      getAdapter().clear();
      setEmpty();
    }
  }

  @Override
  protected RecyclerView.LayoutManager getLayoutManager() {
    return new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_layout_columns));
  }

  @Override
  protected void loadArguments() {
    if (getArguments() != null) {
      username = getArguments().getString(USERNAME);
    }
  }

  @Override
  protected Octicons.Icon getNoDataIcon() {
    return Octicons.Icon.oct_organization;
  }

  @Override
  protected int getNoDataText() {
    return R.string.no_organizations;
  }
}

