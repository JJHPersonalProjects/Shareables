using UnityEngine;
using System.Collections;

public class PickupNoDestroy : MonoBehaviour {

    void OnTriggerEnter(Collider col)
    {
        PlayerManager.health += 50;
       
    }
    // Use this for initialization
    void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
