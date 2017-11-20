package dbaware;

public interface IdbConnector {

	/**
	 * Сохранение лексемы в DB. Функция проверяет, нет ли уже слова в словаре.
	 * Если слово отсутствует, то происходит добавление.
	 * 
	 * @return ID лексемы
	 */
	long SaveLex(String word);

	/**
	 * Поиск слова в словаре. Затем надо искать его по словоформе в справчнике
	 * лексем.
	 * 
	 * @return ID лексемы
	 */
	long GetWord(String rword);

	/**
	 * Сохранение фразы в DB. Если ph_id == -1, то Insert, иначе - Update
	 * 
	 * @param ph_id
	 * @return ID фразы
	 */
	long SavePhrase(long ph_id);

	/**
	 * Сохранение состава фразы в DB. 
	 * 
	 * @param ph_id ID фразы
	 * @param w_id ID лексемы
	 * 
	 * @return ID записи
	 */
	long SavePhraseContent(long ph_id, long w_id);

}
