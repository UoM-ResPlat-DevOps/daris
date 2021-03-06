package daris.client.model;

import daris.client.model.object.DObject;

public class IDUtil {

    public static final String PROJECT_ID_ROOT_NAME = "pssd.project";

    public static final String RSUBJECT_ID_ROOT_NAME = "pssd.r-subject";

    public static final String METHOD_ID_ROOT_NAME = "pssd.method";

    public static final String TRANSFORM_ID_ROOT_NAME = "pssd.transform";

    /**
     * Depth of the project id root. e.g. 1.5
     */
    public static final int PROJECT_ID_ROOT_DEPTH = 2;

    /**
     * Depth of the project id. e.g. 1.5.1
     */
    public static final int PROJECT_ID_DEPTH = 3;

    /**
     * Depth of the subject id. e.g. 1.5.1.1
     */
    public static final int SUBJECT_ID_DEPTH = 4;
    /**
     * Depth of the ex-method id. e.g. 1.5.1.1.1
     */
    public static final int EX_METHOD_ID_DEPTH = 5;
    /**
     * Depth of the study id. e.g. 1.5.1.1.1.1
     */
    public static final int STUDY_ID_DEPTH = 6;
    /**
     * Depth of the dataset id. e.g. 1.5.1.1.1.1.1
     */
    public static final int DATASET_ID_DEPTH = 7;
    /**
     * Depth of the data-object id. e.g. 1.5.1.1.1.1.1.1
     */
    public static final int DATA_OBJECT_ID_DEPTH = 8;

    /**
     * Check if the string is a citeable id.
     * 
     * @param s
     * @return
     */
    public static boolean isCiteableId(String s) {
        if (s == null) {
            return true;
        }
        return s.matches("^\\d+(\\d*.)*\\d+$");

    }

    /**
     * Returns the project id.
     * 
     * @param id
     * @return
     */
    public static String getProjectId(String id) {

        int depth = getIdDepth(id);
        if (depth < PROJECT_ID_DEPTH || depth > DATA_OBJECT_ID_DEPTH) {
            return null;
        }
        int diff = depth - PROJECT_ID_DEPTH;
        return getParentId(id, diff);

    }

    /**
     * Returns the parent id.
     * 
     * @param id
     * @return
     */
    public static String getParentId(String id) {

        if (id == null) {
            return null;
        }
        int idx = id.lastIndexOf('.');
        if (idx == -1) {
            return null;
        }
        return id.substring(0, idx);

    }

    /**
     * Returns the parent/ancester id by specifying the levels.
     * 
     * @param id
     * @param levels
     * @return
     */

    public static String getParentId(String id, int levels) {

        for (int i = 0; i < levels; i++) {
            id = getParentId(id);
        }
        return id;

    }

    /**
     * Depth of the given identifier. This is the number of dots.
     * 
     * @param id
     * @return
     */
    public static int getIdDepth(String id) {

        if (id == null || id.length() == 0) {
            return 0;
        }
        int depth = 1;
        int idx = id.indexOf('.');
        while (idx != -1) {
            depth++;
            idx = id.indexOf('.', idx + 1);
        }
        return depth;

    }

    /**
     * Return the last section of the specified citeable id. e.g. the last section of 1.2.3 is 3
     * 
     * @param id
     * @return
     */
    public static String getLastSection(String id) {

        int idx = id.lastIndexOf('.');
        if (idx == -1) {
            return id;
        }
        return id.substring(idx + 1);

    }

    public static int getLastNumber(String id) {

        if (!isCiteableId(id)) {
            return 0;
        } else {
            return Integer.parseInt(getLastSection(id));
        }
    }

    /**
     * Replace left sections of the citeable id with the specified string.
     * 
     * @param id
     * @param leftSections
     * @return
     */
    public static String replaceLeftSections(String id, String leftSections) {

        if (getIdDepth(id) < getIdDepth(leftSections)) {
            return null;
        }
        if (getIdDepth(id) == getIdDepth(leftSections)) {
            return leftSections;
        }
        String[] parts1 = id.split("\\.");
        String[] parts2 = leftSections.split("\\.");

        for (int i = 0; i < parts2.length; i++) {
            parts1[i] = parts2[i];
        }
        String newCid = parts1[0];
        for (int j = 1; j < parts1.length; j++) {
            newCid = newCid + "." + parts1[j];
        }
        return newCid;

    }

    public static boolean isProjectId(String id) {

        if (!isCiteableId(id)) {
            return false;
        }
        if (getIdDepth(id) == PROJECT_ID_DEPTH) {
            return true;
        }
        return false;

    }

    public static boolean isSubjectId(String id) {

        if (!isCiteableId(id)) {
            return false;
        }
        if (getIdDepth(id) == SUBJECT_ID_DEPTH) {
            return true;
        }
        return false;

    }

    public static boolean isExMethodId(String id) {

        if (!isCiteableId(id)) {
            return false;
        }
        if (getIdDepth(id) == EX_METHOD_ID_DEPTH) {
            return true;
        }
        return false;

    }

    public static boolean isStudyId(String id) {

        if (!isCiteableId(id)) {
            return false;
        }
        if (getIdDepth(id) == STUDY_ID_DEPTH) {
            return true;
        }
        return false;

    }

    public static boolean isDataSetId(String id) {

        if (!isCiteableId(id)) {
            return false;
        }
        if (getIdDepth(id) == DATASET_ID_DEPTH) {
            return true;
        }
        return false;

    }

    public static boolean isDataObjectId(String id) {

        if (!isCiteableId(id)) {
            return false;
        }
        if (getIdDepth(id) == DATA_OBJECT_ID_DEPTH) {
            return true;
        }
        return false;

    }

    public static boolean isProjectIdSegment(String s) {

        if (!isCiteableId(s)) {
            return false;
        }
        if (getIdDepth(s) == PROJECT_ID_DEPTH - PROJECT_ID_ROOT_DEPTH) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isSubjectIdSegment(String s) {

        if (!isCiteableId(s)) {
            return false;
        }
        if (getIdDepth(s) == SUBJECT_ID_DEPTH - PROJECT_ID_ROOT_DEPTH) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isExMethodIdSegment(String s) {

        if (!isCiteableId(s)) {
            return false;
        }
        if (getIdDepth(s) == EX_METHOD_ID_DEPTH - PROJECT_ID_ROOT_DEPTH) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isStudyIdSegment(String s) {

        if (!isCiteableId(s)) {
            return false;
        }
        if (getIdDepth(s) == STUDY_ID_DEPTH - PROJECT_ID_ROOT_DEPTH) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isDataSetIdSegment(String s) {

        if (!isCiteableId(s)) {
            return false;
        }
        if (getIdDepth(s) == DATASET_ID_DEPTH - PROJECT_ID_ROOT_DEPTH) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isDataObjectIdSegment(String s) {

        if (!isCiteableId(s)) {
            return false;
        }
        if (getIdDepth(s) == DATA_OBJECT_ID_DEPTH - PROJECT_ID_ROOT_DEPTH) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Check if the specified pid is the parent of the specified id.
     * 
     * @param pid
     * @param id
     * @return
     */

    public static boolean isParent(String pid, String id) {

        if (id.startsWith(pid)) {
            if (getIdDepth(pid) + 1 == getIdDepth(id)) {
                return true;
            }
        }
        return false;

    }

    public static int compare(String id1, String id2) {

        assert id1 != null && id2 != null;
        if (id1.equals(id2)) {
            return 0;
        }
        String[] parts1 = id1.split("\\.");
        String[] parts2 = id2.split("\\.");
        if (parts1.length < parts2.length) {
            return -1;
        }
        if (parts1.length > parts2.length) {
            return 1;
        }
        for (int i = 0; i < parts1.length; i++) {
            if (!parts1[i].equals(parts2[i])) {
                long n1 = Long.parseLong(parts1[i]);
                long n2 = Long.parseLong(parts2[i]);
                if (n1 < n2) {
                    return -1;
                }
                if (n1 > n2) {
                    return 1;
                }
            }
        }
        return 0;
    }

    public static DObject.Type typeFromIdDepth(int depth) {

        switch (depth) {
        case PROJECT_ID_DEPTH:
            return DObject.Type.project;
        case SUBJECT_ID_DEPTH:
            return DObject.Type.subject;
        case EX_METHOD_ID_DEPTH:
            return DObject.Type.ex_method;
        case STUDY_ID_DEPTH:
            return DObject.Type.study;
        case DATASET_ID_DEPTH:
            return DObject.Type.dataset;
        case DATA_OBJECT_ID_DEPTH:
            return DObject.Type.data_object;
        default:
            return null;
        }
    }

    public static String typeNameFromId(String id) {
        DObject.Type type = typeFromId(id);
        return type == null ? null : type.toString();
    }

    public static DObject.Type typeFromId(String id) {

        int depth = getIdDepth(id);
        if (depth <= 1) {
            // must be a repository (id==null)
            return DObject.Type.repository;
        }
        return typeFromIdDepth(depth);
    }

    public static DObject.Type childTypeFromId(String id) {

        int depth = getIdDepth(id);
        if (depth <= 1) {
            // must be a repository (id==null or id==uuid)
            return DObject.Type.project;
        }
        return typeFromIdDepth(getIdDepth(id) + 1);
    }

    public static boolean isOrIsDescendant(String cid1, String cid2) {
        if (cid2 == null) {
            return true;
        }
        if (cid1 == null) {
            return false;
        }
        return cid1.startsWith(cid2);
    }

    public static String getSubjectId(String id) {
        int depth = getIdDepth(id);
        if (depth < SUBJECT_ID_DEPTH || depth > DATA_OBJECT_ID_DEPTH) {
            return null;
        }
        int diff = depth - SUBJECT_ID_DEPTH;
        return getParentId(id, diff);
    }

    public static String getExMethodId(String id) {
        int depth = getIdDepth(id);
        if (depth < EX_METHOD_ID_DEPTH || depth > DATA_OBJECT_ID_DEPTH) {
            return null;
        }
        int diff = depth - EX_METHOD_ID_DEPTH;
        return getParentId(id, diff);
    }

    public static String getStudyId(String id) {
        int depth = getIdDepth(id);
        if (depth < STUDY_ID_DEPTH || depth > DATA_OBJECT_ID_DEPTH) {
            return null;
        }
        int diff = depth - STUDY_ID_DEPTH;
        return getParentId(id, diff);
    }

    public static boolean isDirectParent(String parent, String child) {
        if (parent == null && isProjectId(child)) {
            return true;
        }
        if (child != null && parent != null) {
            if (getIdDepth(parent) + 1 == getIdDepth(child) && child.startsWith(parent)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDirectChild(String child, String parent) {
        return isDirectParent(parent, child);
    }
}
