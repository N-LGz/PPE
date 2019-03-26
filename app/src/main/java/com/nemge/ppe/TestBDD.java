package com.nemge.ppe;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.nemge.ppe.Database.UserRepository;
import com.nemge.ppe.Local.UserDataSource;
import com.nemge.ppe.Local.UserDatabase;
import com.nemge.ppe.Model.User;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestBDD extends AppCompatActivity {

    private ListView lstUsers;
    private FloatingActionButton fab;

    //Adapter
    List<User> userList = new ArrayList<>();
    ArrayAdapter adapter;

    //Database
    private CompositeDisposable compositeDisposable;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bdd);

        //Actionbar
        setSupportActionBar(findViewById(R.id.home_toolbar));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        compositeDisposable = new CompositeDisposable();

        lstUsers = findViewById(R.id.lstUsers);
        fab = findViewById(R.id.fab);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, userList);
        registerForContextMenu(lstUsers);
        lstUsers.setAdapter(adapter);

        UserDatabase userDatabase = UserDatabase.getInstance(this);//Create database
        userRepository = UserRepository.getInstance(UserDataSource.getInstance(userDatabase.userDAO()));

        loadData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Disposable disposable = (Disposable) io.reactivex.Observable.create(new ObservableOnSubscribe<Object>()
                {
                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception
                    {
                        User user = new User("2016-08-04");
                        userRepository.insertUser(user);
                        e.onComplete();
                    }
                })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer() {
                            @Override
                            public void accept(Object o) throws Exception {
                                Toast.makeText(TestBDD.this, "User added !", Toast.LENGTH_SHORT).show();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(TestBDD.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {
                                loadData();//Refresh data
                            }
                        });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bdd_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_clear:
                deleteAllUsers();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu,  View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(contextMenu, view, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        contextMenu.setHeaderTitle("Select action:");
        contextMenu.add(Menu.NONE, 0, Menu.NONE, "Update");
        contextMenu.add(Menu.NONE, 1, Menu.NONE, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final User user = userList.get(info.position);
        switch (item.getItemId()) {
            case 0: //Update
            {
                EditText edtName = new EditText(TestBDD.this);
                edtName.setText(user.getName());
                edtName.setHint("Enter your name:");
                new AlertDialog.Builder(TestBDD.this)
                        .setTitle("Edit")
                        .setMessage("Edit user name")
                        .setView(edtName)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               if (TextUtils.isEmpty(edtName.getText().toString())) {
                                   return;
                               }
                               else {
                                   user.setName(edtName.getText().toString());
                                   updateUser(user);
                               }
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
            break;
            case 1 ://Delete
            {
                new AlertDialog.Builder(TestBDD.this)
                        .setMessage("Do you want to delete" + user.toString())
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUser(user);
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
            break;
        }
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void deleteUser(User user) {
        Disposable disposable = (Disposable) io.reactivex.Observable.create(new ObservableOnSubscribe<Object>()
        {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception
            {
                userRepository.deleteUser(user);
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        //Toast.makeText(TestBDD.this, "User added !", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(TestBDD.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadData();//Refresh data
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void updateUser(User user) {
        Disposable disposable = (Disposable) io.reactivex.Observable.create(new ObservableOnSubscribe<Object>()
        {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception
            {
                userRepository.updateUser(user);
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        //Toast.makeText(TestBDD.this, "User added !", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(TestBDD.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadData();//Refresh data
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void loadData() {

        Disposable disposable = userRepository.getAllUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<User>>() {
                               @Override
                               public void accept(List<User> users) throws Exception {
                                   onGetAllUserSuccess(users);
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(TestBDD.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           });
        compositeDisposable.add(disposable);
    }

    private void onGetAllUserSuccess(List<User> users) {
        userList.clear();
        userList.addAll(users);
        adapter.notifyDataSetChanged();
    }

    private void deleteAllUsers() {
        Disposable disposable = (Disposable) io.reactivex.Observable.create(new ObservableOnSubscribe<Object>()
        {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception
            {
                userRepository.deleteAllUsers();
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer()
                {
                    @Override
                    public void accept(Object o) throws Exception
                    {
                        //Toast.makeText(TestBDD.this, "User added !", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        Toast.makeText(TestBDD.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action()
                {
                    @Override
                    public void run() throws Exception {
                        loadData();//Refresh data
                    }
                });
        compositeDisposable.add(disposable);
    }

}
