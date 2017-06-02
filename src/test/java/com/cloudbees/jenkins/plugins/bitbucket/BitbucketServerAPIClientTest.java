package com.cloudbees.jenkins.plugins.bitbucket;

import com.cloudbees.jenkins.plugins.bitbucket.server.client.BitbucketServerAPIClient;
import org.junit.Test;
import static org.junit.Assert.*;

public class BitbucketServerAPIClientTest {

    @Test
    public void getOwnerReturnsOwner() throws Exception {
        BitbucketServerAPIClient api = new BitbucketServerAPIClient("test", "clayton", "stuff", null, true, true);
        assertEquals("clayton", api.getOwner());
    }

    @Test
    public void getUserCentricOwnerFormatsOwner() throws Exception {
        BitbucketServerAPIClient apiWithOwner = new BitbucketServerAPIClient("test", "clayton", "stuff", null, true, true);
        assertEquals("~clayton", apiWithOwner.getUserCentricOwner());
    }

    @Test
    public void getUserCentrifOwnerReturnsUnformattedOwnerWhenUserCentficFalse() throws Exception {
        BitbucketServerAPIClient apiWithoutOwner = new BitbucketServerAPIClient("test", "clayton", "stuff", null, false, true);
        assertEquals("clayton", apiWithoutOwner.getUserCentricOwner());
    }
}
