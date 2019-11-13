package ru.tzhack.facegame.facetraking.mlkit

interface MlKitListener {

    /**
     * Метод вызывается, когда игрок наклоняет голову влево/вправо,
     * чтобы управлять персонажем.
     *
     * @param headEulerAngleZ угол наклона головы игрока.
     * */
    fun onHeroHorizontalAnim(headEulerAngleZ: Float)

    /**
     * Метод вызывается, когда игрок поднимает/опускает брови,
     * чтобы ускорить/замедлить персонажа.
     *
     * @param speedValue величина скорости.
     * */
    fun onHeroSpeedAnim(speedValue: Float)

    /**
     * Метод вызывается, когда игрок улыбается, чтобы
     * активировать суперспособность персонажа.
     * */
    fun onHeroSuperPowerAnim()

    /**
     * Метод вызывается, когда игрок подмигивает правым глазом.
     * */
    fun onHeroRightEyeAnim()

    /**
     * Метод вызывается, когда игрок подмигивает левым глазом.
     * */
    fun onHeroLeftEyeAnim()

    /**
     * Метод для вывода ошибок в работе MlKit.
     *
     * @param exception информация об ошибке.
     * */
    fun onError(exception: Exception)
}