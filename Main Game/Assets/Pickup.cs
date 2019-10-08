using UnityEngine;
using System.Collections;

public class Pickup : MonoBehaviour {

	void OnTriggerEnter(Collider col){
		PlayerManager.health +=15;
		Destroy (this.gameObject);
	}

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
