import { Component, OnInit } from '@angular/core';
import * as $ from 'jquery';

declare var window: any;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'image App';
  videostream: any;
    

  ngOnInit() {
      
  }



  public uploadFile(event) {
    console.log('init');
    console.log(event.target.files);
   
  }

  public captureImage() {   
        console.log('captured');
    $(document).ready(() => {
      let video = $('#video').get(0);
      console.log(video);
          let canvas = document.createElement('canvas');
          let imageCanvas = canvas.getContext('2d');
          canvas.width = video.videoWidth/3;
          canvas.height = video.videoHeight/3;
          imageCanvas.drawImage(video, 0, 0, canvas.width, canvas.height);

          var newImageToAppend = document.createElement('IMG');
          newImageToAppend['src'] = canvas.toDataURL();
         // console.log(canvas.toDataURL().length);    // 461333    77214
          document.getElementById('imageToAdd').appendChild(newImageToAppend);
        });
  }

  public processStream(stream) {    

  }



  public startCamera() {
   
    $(document).ready(()=> {
    let video = $('#video').get(0);  
      // Get access to the camera!
      if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        // Not adding `{ audio: true }` since we only want video now
        navigator.mediaDevices.getUserMedia({ video: true }).then( (stream)=> {
          video.srcObject = stream;
          video.play();
          this.videostream = stream; 
          video.onloadedmetadata = function (e) {
            console.log('here we go');            
          };                
        });
      }

    });
  }


 


}
