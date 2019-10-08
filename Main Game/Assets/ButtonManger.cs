using UnityEngine;
using System.Collections;
using UnityEngine.SceneManagement;

public class ButtonManger : MonoBehaviour {


	public void NewGameBtn(string newGameLevel){
		SceneManager.LoadScene (newGameLevel);
	}

	public void QuitGameBtn(){
		Application.Quit ();
	}

	public void HelpBtn(string newScreen){
		SceneManager.LoadScene (newScreen);
	}
	public void MenuBtn(string newScreen){
		SceneManager.LoadScene (newScreen);
	}
}
