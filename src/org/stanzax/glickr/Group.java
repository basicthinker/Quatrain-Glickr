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
	JAPAN("37996572902@N01", "Japan"),
	AUSTRIA("77073770@N00", "Austria"),
	GERMANY("51035715323@N01", "Germany"),
	MALAYSIA("52242384812@N01", "Malaysia"),
	BHUTAN("33499085@N00", "Bhutan"),
	GREAT_BRITAIN("60385233@N00", "Britain"),
	GREECE("44124303046@N01", "Greece"),
	ARGENTINA("43671131820@N01", "Argentina"),
	
	CHINA("52240328087@N01", "China"),
	KOREA("33128065@N00", "Korea"),
	INDIA("52242377700@N01", "India"),
	THAILAND("52242280377@N01", "Thailand"),
	KAZAKHSTAN("89233834@N00", "Kazakhstan"),
	LAOS("52242386987@N01", "Laos"),
	IRAN("34637060@N00", "Iran"),
	NEPAL("81192396@N00", "Nepal"),
	
	USA("74744754@N00", "USA"),
	CANADA("52240173757@N01", "Canada"),
	MEXICO("38054612@N00", "Mexico"),
	JAMAICA("49391284@N00", "Jamaica"),
	CUBA("905730@N21", "Cuba"),
	
	BRAZIL("45331594@N00", "Brazil"),
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
