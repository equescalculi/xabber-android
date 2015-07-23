package com.xabber.android.ui;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xabber.android.R;
import com.xabber.android.data.account.AccountManager;
import com.xabber.android.data.extension.avatar.AvatarManager;
import com.xabber.android.data.extension.muc.MUCManager;
import com.xabber.android.data.extension.muc.RoomInvite;

import org.jivesoftware.smack.util.StringUtils;

public class ConferenceAddFragment extends Fragment {

    protected static final String ARG_ACCOUNT = "com.xabber.android.ui.ConferenceAddFragment.ARG_ACCOUNT";
    protected static final String ARG_ROOM = "com.xabber.android.ui.ConferenceAddFragment.ARG_ROOM";

    private EditText nickView;
    private EditText passwordView;

    private String account = null;
    private String room = null;

    public static ConferenceAddFragment newInstance(String account, String room) {
        ConferenceAddFragment fragment = new ConferenceAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCOUNT, account);
        args.putString(ARG_ROOM, room);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            account = getArguments().getString(ARG_ACCOUNT);
            room = getArguments().getString(ARG_ROOM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conference_add_fragment, container, false);

        ((TextView) view.findViewById(R.id.muc_conference_jid)).setText(room);
        ((TextView) view.findViewById(R.id.muc_account_jid)).setText(StringUtils.parseBareAddress(account));

        Drawable accountAvatar = AvatarManager.getInstance().getAccountAvatar(account);
        int h = accountAvatar.getIntrinsicHeight();
        int w = accountAvatar.getIntrinsicWidth();
        accountAvatar.setBounds( 0, 0, w, h );
        ((TextView) view.findViewById(R.id.muc_account_jid)).setCompoundDrawables(accountAvatar, null, null, null);

        nickView = (EditText) view.findViewById(R.id.muc_nick);
        nickView.setText(MUCManager.getInstance().getNickname(account, room));
        if ("".equals(nickView.getText().toString())) {
            nickView.setText(getNickname(account));
        }

        passwordView = (EditText) view.findViewById(R.id.muc_password);
        String password;
        RoomInvite roomInvite = MUCManager.getInstance().getInvite(account, room);
        if (roomInvite != null) {
            password = roomInvite.getPassword();
        } else {
            password = MUCManager.getInstance().getPassword(account, room);
        }
        passwordView.setText(password);

        MUCManager.getInstance().removeAuthorizationError(account, room);

        setHasOptionsMenu(true);

        return view;
    }


    /**
     * @return Suggested nickname in the room.
     */
    private String getNickname(String account) {
        if (account == null) {
            return "";
        }
        String nickname = AccountManager.getInstance().getNickName(account);
        String name = StringUtils.parseName(nickname);
        if ("".equals(name)) {
            return nickname;
        } else {
            return name;
        }
    }

    private void addConference() {
        String nick = nickView.getText().toString();
        if ("".equals(nick)) {
            Toast.makeText(getActivity(), getString(R.string.EMPTY_NICK_NAME), Toast.LENGTH_LONG).show();
            return;
        }
        String password = passwordView.getText().toString();
        final boolean join = true;
        MUCManager.getInstance().createRoom(account, room, nick, password, join);
        getActivity().finish();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_conference, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_conference:
                addConference();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}