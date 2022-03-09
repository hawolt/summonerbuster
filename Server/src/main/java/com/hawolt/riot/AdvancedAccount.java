package com.hawolt.riot;

import com.hawolt.Logger;
import com.hawolt.bulk.Account;
import com.hawolt.bulk.DispatchQueue;
import com.hawolt.bulk.Region;
import com.hawolt.bulk.Token;
import com.hawolt.bulk.task.AbstractTask;
import com.hawolt.bulk.task.TaskCallback;
import com.hawolt.riot.task.impl.GetEntitlement;
import com.hawolt.riot.task.impl.GetIdentifier;
import com.hawolt.riot.task.impl.GetPlatform;
import com.hawolt.riot.task.impl.GetSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;

/**
 * Created: 07/03/2022 17:57
 * Author: Twitter @hawolt
 **/

public class AdvancedAccount {

    private final SessionPreparationCallback callback;
    private final Account account;

    private final AbstractTask[] tasks = new AbstractTask[]{
            new GetIdentifier(null, this),
            new GetEntitlement(null, this),
            new GetPlatform(null, this),
            new GetSession(null, this)
    };

    private int index = -1;

    private AdvancedAccount(SessionPreparationCallback callback, Account account) {
        this.callback = callback;
        this.account = account;
        this.setup();
        this.next();
    }

    public static void init(SessionPreparationCallback callback, Account account) {
        new AdvancedAccount(callback, account);
    }

    private String host, sub, uname;
    private Region region, origin;
    private long cuid;

    private void setup() {
        try {
            String id_token = this.account.get(Token.ID);
            JSONObject identifier = new JSONObject(new String(Base64.getDecoder().decode(id_token.split("\\.")[1])));
            JSONArray lol = identifier.getJSONArray("lol");
            JSONObject account = lol.getJSONObject(0);

            this.cuid = account.getLong("cuid");
            this.sub = identifier.getString("sub");
            this.region = Region.findByName(account.getString("cpid"));
            this.origin = account.isNull("ptrid") ? getRegion() : Region.findByName(account.getString("ptrid"));
            this.uname = account.getString("uname");

            switch (region) {
                case NA:
                case LA1:
                case LA2:
                case OC1:
                case BR:
                    this.host = "usw.pp.riotgames.com";
                    break;
                case EUW:
                case EUNE:
                case TR:
                case RU:
                    this.host = "euc.pp.riotgames.com";
                    break;
                case JP:
                case KR:
                    this.host = "apne.pp.riotgames.com";
                    break;
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    public String getPUUID() {
        return sub;
    }

    public String getUname() {
        return uname;
    }

    public Region getOrigin() {
        return origin;
    }

    public long getCUID() {
        return cuid;
    }

    public void next() {
        int next = ++index;
        System.out.println(next + " " + account.getLock());
        if (next >= tasks.length) {
            callback.onPrepared(this);
        } else {
            DispatchQueue.submit(tasks[index]);
        }
    }

    public void repeat() {
        DispatchQueue.submit(tasks[index--]);
    }

    public Account getAccount() {
        return account;
    }

    private String identifier;

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    private String entitlement;

    public void setEntitlement(String entitlement) {
        this.entitlement = entitlement;
    }

    public String getEntitlement() {
        return entitlement;
    }

    public String getHost() {
        return host;
    }

    public Region getRegion() {
        return region;
    }

    private String platform;

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    private String session;

    public void setSession(String session) {
        this.session = session;
    }

    public String getSession() {
        return session.substring(1, session.length() - 1);
    }
}
