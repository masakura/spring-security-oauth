/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.oauth2.provider.vote;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.AccessGrantAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * @author Dave Syer
 * 
 */
public class TestScopeVoter {

	private ScopeVoter voter = new ScopeVoter();

	@Test
	public void testAbstainIfNotOAuth2() throws Exception {
		Authentication clientAuthentication = new AccessGrantAuthenticationToken("foo", null, Collections.singleton("read"), "access_token");
		assertEquals(AccessDecisionVoter.ACCESS_ABSTAIN, voter.vote(clientAuthentication, null, Collections.<ConfigAttribute>singleton(new SecurityConfig("SCOPE_READ"))));
	}

	@Test
	public void testAbstainIfNotClientToken() throws Exception {
		Authentication clientAuthentication = new UsernamePasswordAuthenticationToken("foo", "bar", Collections.singleton(new SimpleGrantedAuthority("ROLE_FOO")));
		Authentication userAuthentication = null;
		OAuth2Authentication<Authentication, Authentication> oAuth2Authentication = new OAuth2Authentication<Authentication, Authentication>(clientAuthentication, userAuthentication);
		assertEquals(AccessDecisionVoter.ACCESS_ABSTAIN, voter.vote(oAuth2Authentication, null, Collections.<ConfigAttribute>singleton(new SecurityConfig("SCOPE_READ"))));
	}

	@Test
	public void testAccessGrantedIfScopesPresent() throws Exception {
		Authentication clientAuthentication = new AccessGrantAuthenticationToken("foo", null, Collections.singleton("read"), "access_token");
		Authentication userAuthentication = null;
		OAuth2Authentication<Authentication, Authentication> oAuth2Authentication = new OAuth2Authentication<Authentication, Authentication>(clientAuthentication, userAuthentication);
		assertEquals(AccessDecisionVoter.ACCESS_GRANTED, voter.vote(oAuth2Authentication, null, Collections.<ConfigAttribute>singleton(new SecurityConfig("SCOPE_READ"))));
	}

	@Test
	public void testAccessDeniedIfWrongScopesPresent() throws Exception {
		Authentication clientAuthentication = new AccessGrantAuthenticationToken("foo", null, Collections.singleton("read"), "access_token");
		Authentication userAuthentication = null;
		OAuth2Authentication<Authentication, Authentication> oAuth2Authentication = new OAuth2Authentication<Authentication, Authentication>(clientAuthentication, userAuthentication);
		assertEquals(AccessDecisionVoter.ACCESS_DENIED, voter.vote(oAuth2Authentication, null, Collections.<ConfigAttribute>singleton(new SecurityConfig("SCOPE_WRITE"))));
	}
}
