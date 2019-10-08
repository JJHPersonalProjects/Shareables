using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using System.Collections;


public class PlayerManager : MonoBehaviour
{

    public static int health = 100;
    public GameObject player;
    public Slider healthBar;
    public Canvas canvas;

    // Use this for initialization
    void Start()
    {
        InvokeRepeating("checkHealth", 1, 1);
       // StartCoroutine(BlinkTimer());
    }

    void checkHealth()
    {
        health -= 2;
        healthBar.value = health;

        if (health <= 0)
        {
            StartCoroutine(BlinkTimer());
            player.GetComponent<Animator>().SetBool("isDead", true);
         
            //canvas.enabled = false;
            //creat timer for 5 seconds 

            //if timer equals five  load start menu
            //BlinkTimer();

            //        SceneManager.LoadScene("Menu"); //should be inside if statement
        }
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetKeyDown(KeyCode.Mouse0))// if left click
        {
            Application.LoadLevel(Application.loadedLevel);
            health = 100;
        }
    }



    IEnumerator BlinkTimer()
    {
        yield return new WaitForSeconds(6); //waits couple seconds before rturning to menu
        SceneManager.LoadScene("Menu");
        health = 100;
       
    }
}