/**
 * 
 */
package org.stanzax.glickr;

/**
 * @author basicthinker
 *
 */
public enum Group {
	SPAIN("37996655186@N01", "Spain"),
	SWITZERLAND("41894179852@N01", "Switzerland"),
	JAPAN("37996572902@N01", "flickr Japan"),
	AUSTRIA("77073770@N00", "Austria"),
	GERMANY("51035715323@N01", "Germany"),
	MALAYSIA("52242384812@N01", "Malaysia Images"),
	BHUTAN("33499085@N00", "Bhutan Images"),
	GREAT_BRITAIN("60385233@N00", "Great Britain"),
	GREECE("44124303046@N01", "GREECE!"),
	ARGENTINA("43671131820@N01", "Argentina"),
	
	CHINA("52240328087@N01", "China"),
	KOREA("33128065@N00", "Korea images"),
	INDIA("52242377700@N01", "India Images"),
	THAILAND("52242280377@N01", "Thailand Images"),
	KAZAKHSTAN("89233834@N00", "Kazakhstan"),
	LAOS("52242386987@N01", "Laos Images"),
	IRAN("34637060@N00", "Iran"),
	NEPAL("81192396@N00", "Nepal Images"),
	
	USA("74744754@N00", "USA - UNITED STATES OF AMERICA"),
	CANADA("52240173757@N01", "Canadian Beauty"),
	MEXICO("38054612@N00", "Mexico"),
	JAMAICA("49391284@N00", "Jamaica, West Indies"),
	CUBA("905730@N21", "Cuba Collection"),
	
	BRAZIL("45331594@N00", "Brasil/Brazil"),
	BOLIVIA("10184953@N00", "Bolivia"),
	PARAGUAY("31265048@N00", "Paraguay"),
	URUGUAY("90096392@N00", "Uruguay"),
	GUYANA("24463356@N00", "Guyana"),
	
	ENGLAND("35468144964@N01", "England"),
	FRANCE("52240442714@N01", "France");
	
	Group(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	String getID() {
		return id;
	}
	
	String getName() {
		return name;
	}
	
	private final String id;
	private final String name;
}
