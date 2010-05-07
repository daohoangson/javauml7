package com.daohoangson.uml.structures;

public class Structure implements StructureListener {
	private String name;
	private String type;
	static private int VISIBILITY_DEFAULT;
	static private int VISIBILITY_PUBLIC;
	static private int VISIBILITY_PROTECTED;
	static private int VISIBILITY_PRIVATE;
	private int visibility;
	private boolean is_static;
	private Vector<StructureListener> listeners;
	private Structure container;
	private List<Structure> parents;
	private List<Structure> children;
	private Hashtable<String, String> info;
	protected boolean cfg_unique_globally;
	protected boolean cfg_use_type;
	protected boolean cfg_use_visibility;
	protected boolean cfg_use_scope;
	protected String[] cfg_container_structures;
	protected String[] cfg_parent_structures;
	protected String[] cfg_child_structures;
	protected boolean cfg_hide_visibility;
	protected boolean cfg_hide_children;
	protected String cfg_regex_name;
	protected String cfg_regex_type;
	static private Hashtable<String, Structure> names;
	static private List<StructureAdding> adding_queue;
	static public StructureListener global_listener;
	static public boolean debuging;

	protected void config() {
		// TODO Auto-generated method stub
	}

	public Structure() {
		// TODO Auto-generated constructor stub
	}

	public boolean setName(String name) {
		// TODO Auto-generated method stub
	}

	public boolean setType(String type) {
		// TODO Auto-generated method stub
	}

	private boolean validateType(String type, boolean split) {
		// TODO Auto-generated method stub
	}

	public boolean setModifier(String modifier) {
		// TODO Auto-generated method stub
	}

	public String toString() {
		// TODO Auto-generated method stub
	}

	public void setInfo(String key, String value) {
		// TODO Auto-generated method stub
	}

	public boolean checkIsUniqueGlobally() {
		// TODO Auto-generated method stub
	}

	public boolean checkUseType() {
		// TODO Auto-generated method stub
	}

	public boolean checkHasChildren() {
		// TODO Auto-generated method stub
	}

	public boolean checkHasParents() {
		// TODO Auto-generated method stub
	}

	public boolean checkIsAlike(Structure that) {
		// TODO Auto-generated method stub
	}

	public boolean checkIsChildOf(Structure that) {
		// TODO Auto-generated method stub
	}

	public boolean checkHasChildLike(Structure that) {
		// TODO Auto-generated method stub
	}

	public String getStructureName() {
		// TODO Auto-generated method stub
	}

	public String getName() {
		// TODO Auto-generated method stub
	}

	static public Structure lookUp(String name) {
		// TODO Auto-generated method stub
	}

	public String getType() {
		// TODO Auto-generated method stub
	}

	public Structure[] getTypeAsStructure() {
		// TODO Auto-generated method stub
	}

	public Structure[] getTypeAsStructure(String type) {
		// TODO Auto-generated method stub
	}

	public String getVisibility() {
		// TODO Auto-generated method stub
	}

	public String getScope() {
		// TODO Auto-generated method stub
	}

	public String getInfo(String key) {
		// TODO Auto-generated method stub
	}

	public Structure getContainer() {
		// TODO Auto-generated method stub
	}

	public Structure[] getParents() {
		// TODO Auto-generated method stub
	}

	public int getParentsCount() {
		// TODO Auto-generated method stub
	}

	public Structure[] getChildren() {
		// TODO Auto-generated method stub
	}

	public int getChildrenCount() {
		// TODO Auto-generated method stub
	}

	public boolean add(Structure that) {
		// TODO Auto-generated method stub
	}

	public void addParentName(String structure_name) {
		// TODO Auto-generated method stub
	}

	private void addParentNameProceed() {
		// TODO Auto-generated method stub
	}

	public boolean remove(Structure that) {
		// TODO Auto-generated method stub
	}

	public void addStructureListener(StructureListener listener) {
		// TODO Auto-generated method stub
	}

	public void removeStructureListener(StructureListener listener) {
		// TODO Auto-generated method stub
	}

	protected void fireChanged() {
		// TODO Auto-generated method stub
	}

	public void structureChanged(StructureEvent e) {
		// TODO Auto-generated method stub
	}

	static private boolean foundStringInArray(String str, String[] array) {
		// TODO Auto-generated method stub
	}

	static public Structure[] filterStructureArray(Structure[] structures, String[] structureNames) {
		// TODO Auto-generated method stub
	}
}