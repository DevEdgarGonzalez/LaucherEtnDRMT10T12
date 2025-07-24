package com.actia.drm;

interface OnUserRegisterListener {
	void userRegistered(int idUser, boolean error, String errorString);
}
