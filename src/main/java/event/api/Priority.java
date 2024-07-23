package event.api;

/**
 * Приоритет подписчика.
 * <p>Во время публикации события подписчики будут вызваны в порядке возрастания их приоритетов.</p>
 */
public enum Priority {
    /* Наименьший приоритет: будет вызван первым. */
    LOWEST,

    LOW,

    /* Стандартный приоритет. */
    NORMAL,

    HIGH,

    /* Наивысший приоритет: будет вызван последним. */
    HIGHEST
}