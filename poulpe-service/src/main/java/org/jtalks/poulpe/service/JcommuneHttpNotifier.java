/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.model.entity.PoulpeSection;
import org.jtalks.poulpe.service.exceptions.JcommuneRespondedWithErrorException;
import org.jtalks.poulpe.service.exceptions.JcommuneUrlNotConfiguratedException;
import org.jtalks.poulpe.service.exceptions.NoConnectionToJcommuneException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Notifier to notify JCommune component about elements' deleting. It is usefull to help forum keep such information,
 * as user's messages count, up to date.
 *
 * @author Nickolay Polyarniy
 * @author Mikhail Zaitsev
 */
public class JcommuneHttpNotifier {

    /**
     * Jcommune url
     */
    private String jCommuneUrl;

    /**
     *  Minimum value of a successful status
     */
    private final int MIN_HTTP_STATUS = 200;

    /**
     *  Minimum value of a successful status
     */
    private final int MAX_HTTP_STATUS = 299;

    /**
     * Whole forum link
     */
    private static final String WHOLEFORUM_URL_PART = "/wholeforum/";

    /**
     * Reindex link
     */
    private static final String REINDEX_URL_PART = "/search/index/rebuild";

    /**
     * Sections link
     */
    private static final String SECTIONS_URL_PART = "/sections/";

    /**
     * Branch link
     */
    private static final String BRANCH_URL_PART = "/branch/";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Notifies delete the section
     *
     * @param section which will be deleted
     * @throws {@link NoConnectionToJcommuneException} some connection problems happend, while trying to notify Jcommune
     * @throws {@link JcommuneRespondedWithErrorException} occurs when the response status is not in the interval
     *         {@code MIN_HTTP_STATUS} and {@code MAX_HTTP_STATUS}
     * @throws {@link JcommuneUrlNotConfiguratedException} occurs when the {@code jCommuneUrl} is incorrect
     */
    public void notifyAboutSectionDelete(PoulpeSection section)
        throws NoConnectionToJcommuneException,JcommuneRespondedWithErrorException,JcommuneUrlNotConfiguratedException{
        long id = section.getId();
        notifyAboutDeleteElement(jCommuneUrl + SECTIONS_URL_PART + id);
    }

    /**
     * Notifies delete the branch
     *
     * @param branch which will be deleted
     * @throws {@link NoConnectionToJcommuneException} some connection problems happend, while trying to notify Jcommune
     * @throws {@link JcommuneRespondedWithErrorException} occurs when the response status is not in the interval
     *         {@code MIN_HTTP_STATUS} and {@code MAX_HTTP_STATUS}
     * @throws {@link JcommuneUrlNotConfiguratedException} occurs when the {@code jCommuneUrl} is incorrect
     */
    public void notifyAboutBranchDelete(PoulpeBranch branch)
        throws NoConnectionToJcommuneException,JcommuneRespondedWithErrorException,JcommuneUrlNotConfiguratedException{
        long id = branch.getId();
        notifyAboutDeleteElement(jCommuneUrl + BRANCH_URL_PART + id);
    }

    /**
     * Notifies delete the component
     *
     * @throws {@link NoConnectionToJcommuneException} some connection problems happend, while trying to notify Jcommune
     * @throws {@link JcommuneRespondedWithErrorException} occurs when the response status is not in the interval
     *         {@code MIN_HTTP_STATUS} and {@code MAX_HTTP_STATUS}
     * @throws {@link JcommuneUrlNotConfiguratedException} occurs when the {@code jCommuneUrl} is incorrect
     */
    public void notifyAboutComponentDelete()
        throws NoConnectionToJcommuneException,JcommuneRespondedWithErrorException,JcommuneUrlNotConfiguratedException{
        notifyAboutDeleteElement(jCommuneUrl + WHOLEFORUM_URL_PART);
    }

    /**
     * Notifies delete the element
     *
     * @throws {@link NoConnectionToJcommuneException} some connection problems happend, while trying to notify Jcommune
     * @throws {@link JcommuneRespondedWithErrorException} occurs when the response status is not in the interval
     *         {@code MIN_HTTP_STATUS} and {@code MAX_HTTP_STATUS}
     * @throws {@link JcommuneUrlNotConfiguratedException} occurs when the {@code jCommuneUrl} is incorrect
     */
    private void notifyAboutDeleteElement(String url)
        throws NoConnectionToJcommuneException,JcommuneRespondedWithErrorException,JcommuneUrlNotConfiguratedException{
        checkUrlAreConfigurated();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpDelete deleteRequest = new HttpDelete(url);
            HttpResponse response = httpClient.execute(deleteRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < MIN_HTTP_STATUS || statusCode > MAX_HTTP_STATUS) {
                throw new JcommuneRespondedWithErrorException();
            }
        } catch (IOException e) {
            throw new NoConnectionToJcommuneException();
        }
    }

    /**
     * Checks the url
     *
     * @throws {@link JcommuneUrlNotConfiguratedException} occurs when the {@code jCommuneUrl} is incorrect
     */
    private void checkUrlAreConfigurated() throws JcommuneUrlNotConfiguratedException {
        if (StringUtils.isBlank(jCommuneUrl)) {
            throw new JcommuneUrlNotConfiguratedException();
        }
    }

    /**
     * Notifies reindex сomponent
     *
     * @throws {@link NoConnectionToJcommuneException} some connection problems happend, while trying to notify Jcommune
     * @throws {@link JcommuneRespondedWithErrorException} occurs when the response status is not in the interval
     *         {@code MIN_HTTP_STATUS} and {@code MAX_HTTP_STATUS}
     * @throws {@link JcommuneUrlNotConfiguratedException} occurs when the {@code jCommuneUrl} is incorrect
     */
    public void notifyAboutReindexComponent()
        throws NoConnectionToJcommuneException,JcommuneRespondedWithErrorException,JcommuneUrlNotConfiguratedException{
        checkUrlAreConfigurated();
        String reindexUrl = jCommuneUrl + REINDEX_URL_PART;
        try {

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost emptyRequest = new HttpPost(reindexUrl);
            HttpResponse response = httpClient.execute(emptyRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < MIN_HTTP_STATUS || statusCode > MAX_HTTP_STATUS) {
                throw new JcommuneRespondedWithErrorException();
            }
        } catch (IOException e) {
            logger.warn("Error sending request to Jcommune: {}. Root cause: ", reindexUrl, e);
            throw new NoConnectionToJcommuneException();
        }
    }

    /**
     * @return  jcommune url
     */
    public String getjCommuneUrl() {
        return jCommuneUrl;
    }

    /**
     * @param jCommuneUrl jcommune url
     */
    public void setjCommuneUrl(String jCommuneUrl) {
        this.jCommuneUrl = jCommuneUrl;
    }


}