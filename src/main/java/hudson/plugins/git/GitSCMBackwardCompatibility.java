package hudson.plugins.git;

import hudson.RelativePath;
import hudson.plugins.git.extensions.GitSCMExtension;
import hudson.plugins.git.extensions.GitSCMExtensionDescriptor;
import hudson.plugins.git.extensions.impl.PathRestriction;
import hudson.plugins.git.extensions.impl.PerBuildTag;
import hudson.plugins.git.extensions.impl.RelativeTargetDirectory;
import hudson.plugins.git.extensions.impl.SubmoduleOption;
import hudson.plugins.git.extensions.impl.UserExclusion;
import hudson.scm.SCM;
import hudson.util.DescribableList;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

/**
 * This is a portion of {@link GitSCM} for the stuff that's used to be in {@link GitSCM}
 * that are deprecated. Moving deprecated stuff from {@link GitSCM} to here allows us
 * to keep {@link GitSCM} cleaner.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class GitSCMBackwardCompatibility extends SCM implements Serializable {
    // old fields are left so that old config data can be read in, but
    // they are deprecated. transient so that they won't show up in XML
    // when writing back
    @Deprecated
    transient String source;
    @Deprecated
    transient String branch;

    /**
     * @deprecated
     *      Replaced by {@link GitSCM#buildChooser} instead.
     */
    transient String choosingStrategy;

    /**
     * @deprecated
     *      Moved to {@link RelativePath}
     */
    private transient String relativeTargetDir;
    /**
     * @deprecated
     *      Moved to {@link PathRestriction}.
     */
    private transient String includedRegions;
    /**
     * @deprecated
     *      Moved to {@link PathRestriction}.
     */
    private transient String excludedRegions;
    /**
     * @deprecated
     *      Moved to {@link UserExclusion}.
     */
    private transient String excludedUsers;

    /**
     * @deprecated
     *      Moved to {@link PerBuildTag}
     */
    private transient Boolean skipTag;


    /**
     * @deprecated
     *      Moved to {@link SubmoduleOption}
     */
    private transient boolean disableSubmodules;

    /**
     * @deprecated
     *      Moved to {@link SubmoduleOption}
     */
    private transient boolean recursiveSubmodules;


    abstract DescribableList<GitSCMExtension, GitSCMExtensionDescriptor> getExtensions();

    void readBackExtensionsFromLegacy() {
        try {
            if (excludedUsers!=null) {
                addIfMissing(new UserExclusion(excludedUsers));
                excludedUsers = null;
            }

            if (excludedRegions!=null || includedRegions!=null) {
                addIfMissing(new PathRestriction(includedRegions, excludedRegions));
                excludedRegions = includedRegions = null;
            }
            if (relativeTargetDir!=null) {
                addIfMissing(new RelativeTargetDirectory(relativeTargetDir));
                relativeTargetDir = null;
            }
            if (skipTag!=null && skipTag) {
                addIfMissing(new PerBuildTag());
                skipTag = null;
            }
            if (disableSubmodules || recursiveSubmodules)
                addIfMissing(new SubmoduleOption(disableSubmodules, recursiveSubmodules));
        } catch (IOException e) {
            throw new AssertionError(e); // since our extensions don't have any real Saveable
        }

    }

    private void addIfMissing(GitSCMExtension ext) throws IOException {
        if (getExtensions().get(ext.getClass())==null)
            getExtensions().add(ext);
    }

    @Deprecated
    public String getIncludedRegions() {
        PathRestriction pr = getExtensions().get(PathRestriction.class);
        return pr!=null ? pr.getIncludedRegions() : null;
    }

    @Deprecated
    public String getExcludedRegions() {
        PathRestriction pr = getExtensions().get(PathRestriction.class);
        return pr!=null ? pr.getExcludedRegions() : null;
    }

    @Deprecated
    public String[] getExcludedRegionsNormalized() {
        PathRestriction pr = getExtensions().get(PathRestriction.class);
        return pr!=null ? pr.getExcludedRegionsNormalized() : null;
    }

    @Deprecated
    public String[] getIncludedRegionsNormalized() {
        PathRestriction pr = getExtensions().get(PathRestriction.class);
        return pr!=null ? pr.getIncludedRegionsNormalized() : null;
    }


    @Deprecated
    public String getRelativeTargetDir() {
        RelativeTargetDirectory rt = getExtensions().get(RelativeTargetDirectory.class);
        return rt!=null ? rt.getRelativeTargetDir() : null;
    }


    @Deprecated
    public String getExcludedUsers() {
        UserExclusion ue = getExtensions().get(UserExclusion.class);
        return ue!=null ? ue.getExcludedUsers() : null;
    }

    @Deprecated
    public Set<String> getExcludedUsersNormalized() {
        UserExclusion ue = getExtensions().get(UserExclusion.class);
        return ue!=null ? ue.getExcludedUsersNormalized() : null;
    }

    @Deprecated
    public boolean getSkipTag() {
        return getExtensions().get(PerBuildTag.class)==null;
    }

    @Deprecated
    public boolean getDisableSubmodules() {
        SubmoduleOption sm = getExtensions().get(SubmoduleOption.class);
        return sm != null && sm.isDisableSubmodules();
    }

    @Deprecated
    public boolean getRecursiveSubmodules() {
        SubmoduleOption sm = getExtensions().get(SubmoduleOption.class);
        return sm != null && sm.isRecursiveSubmodules();
    }


    private static final long serialVersionUID = 1L;
}
