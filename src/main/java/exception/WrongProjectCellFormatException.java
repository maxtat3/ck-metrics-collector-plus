package exception;

/**
 * Недействительный формат ячейки конкретного проекта.
 * То есть в ячейке указаны данные, но скорее всего они не относятся
 * к описанию программного проекта.
 * Примеры: в ячейке номера проекта текст, а не число,
 * в ячейке указания языка программного проекта слишком длинный текст, и т.д..
 * Это зависит от конкретной характеристики (столбца) программного проекта.
 *
 */
public class WrongProjectCellFormatException extends RuntimeException {

	public WrongProjectCellFormatException(String message) {
		super(message);
	}
}
