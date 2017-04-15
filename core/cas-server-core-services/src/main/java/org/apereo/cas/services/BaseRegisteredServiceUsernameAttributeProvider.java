package org.apereo.cas.services;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.services.persondir.util.CaseCanonicalizationMode;

import javax.persistence.PostLoad;
import java.util.Locale;

/**
 * This is {@link BaseRegisteredServiceUsernameAttributeProvider}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public abstract class BaseRegisteredServiceUsernameAttributeProvider implements RegisteredServiceUsernameAttributeProvider {
    private static final long serialVersionUID = -8381275200333399951L;

    private String canonicalizationMode = CaseCanonicalizationMode.NONE.name();
    private boolean encryptUsername;

    public BaseRegisteredServiceUsernameAttributeProvider() {
        setCanonicalizationMode(CaseCanonicalizationMode.NONE.name());
    }

    public BaseRegisteredServiceUsernameAttributeProvider(final String canonicalizationMode) {
        this.canonicalizationMode = canonicalizationMode;
    }

    @Override
    public final String resolveUsername(final Principal principal, final Service service, final RegisteredService registeredService) {
        final String username = resolveUsernameInternal(principal, service);
        if (canonicalizationMode == null) {
            return username;
        }
        final String uid = CaseCanonicalizationMode.valueOf(canonicalizationMode).canonicalize(username.trim(), Locale.getDefault());
        if (this.encryptUsername && registeredService.getPublicKey()) {
            return encryptResolvedUsername(principal, service, registeredService);
        }
        return uid;
    }

    /**
     * Initializes the registered service with default values
     * for fields that are unspecified. Only triggered by JPA.
     */
    @PostLoad
    public void initialize() {
        setCanonicalizationMode(CaseCanonicalizationMode.NONE.name());
    }

    /**
     * Resolve username internal string.
     *
     * @param principal the principal
     * @param service   the service
     * @return the string
     */
    protected abstract String resolveUsernameInternal(Principal principal, Service service);

    public String getCanonicalizationMode() {
        return canonicalizationMode;
    }

    public void setCanonicalizationMode(final String canonicalizationMode) {
        this.canonicalizationMode = canonicalizationMode;
    }

    public boolean isEncryptUsername() {
        return encryptUsername;
    }

    public void setEncryptUsername(final boolean encryptUsername) {
        this.encryptUsername = encryptUsername;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final BaseRegisteredServiceUsernameAttributeProvider rhs = (BaseRegisteredServiceUsernameAttributeProvider) obj;
        return new EqualsBuilder()
                .append(this.canonicalizationMode, rhs.canonicalizationMode)
                .append(this.encryptUsername, rhs.encryptUsername)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(canonicalizationMode)
                .append(encryptUsername)
                .toHashCode();
    }
}
