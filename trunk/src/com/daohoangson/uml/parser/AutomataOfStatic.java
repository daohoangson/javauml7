package com.daohoangson.uml.parser;

class AutomataOfStatic extends Automata {
	private String string;

	public AutomataOfStatic(int id, String string) {
		setId(id);
		this.string = string;
		config();
	}

	public AutomataOfStatic(int id, char c) {
		setId(id);
		string = String.valueOf(c);
		config();
	}

	protected void config() {
		AutomataState last = getStartState();

		for (int i = 0; i < string.length(); i++) {
			AutomataState state = new AutomataState();
			last.add(string.charAt(i), state);
			last = state;
		}

		setAcceptStage(last);
	}

}
