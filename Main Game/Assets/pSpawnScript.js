#pragma strict

var SpawnPoint : Transform[];

function Start () {

}

function Update () {

}

function OnTriggerEnter(man : Collider){

    if(man.GetComponent.<Collider>().tag == "Player"){
        var element : int = Random.Range (0, SpawnPoint.length);
        man.transform.position = SpawnPoint[element].position;
    }
    else{

    }

}