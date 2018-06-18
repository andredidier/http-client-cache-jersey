package com.lealdidier.andre.http.client.cache;

import java.util.Calendar;
import java.util.Date;

public class CacheControlValueExpirationDate implements ExpirationDate {

    private Date dateRequested;
    private CacheControlHeader cacheControlHeader;
    private transient Date expiresAt;

    public CacheControlValueExpirationDate(Date dateRequested, CacheControlHeader cacheControlHeader) {
        this.dateRequested = dateRequested;
        this.cacheControlHeader = cacheControlHeader;
    }

    public CacheControlValueExpirationDate(CacheControlHeader cacheControlHeader) {
        this(new Date(), cacheControlHeader);
    }

    @Override
    public boolean isExpired() {
        if (cacheControlHeader.noStore()) {
            return true;
        }
        if (expiresAt == null) {
            initExpiresAt();
        }
        Date d = new Date();
        return expiresAt.before(d);
    }

    private void initExpiresAt() {
        Calendar c = Calendar.getInstance();
        c.setTime(dateRequested);
        c.add(Calendar.SECOND, cacheControlHeader.maxAge());
        expiresAt = c.getTime();
    }
}
