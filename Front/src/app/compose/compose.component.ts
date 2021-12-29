import { Component, Injectable, OnInit } from '@angular/core';
import { ToolbarService, LinkService, ImageService, HtmlEditorService } from '@syncfusion/ej2-angular-richtexteditor';
import { faPaperclip } from '@fortawesome/free-solid-svg-icons';
import { Location } from '@angular/common'
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { HttpClient, HttpEvent, HttpParams } from '@angular/common/http';
import { Observable, observable } from 'rxjs';
import { map } from 'rxjs';
import { Mail } from '../mail';
import { SharedService } from '../shared/shared.service';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
@Injectable({
  providedIn: 'root' 
})
@Component({ 
  selector: 'app-compose',
  templateUrl: './compose.component.html',
  styleUrls: ['./compose.component.css'],
  providers: [ToolbarService, LinkService, ImageService, HtmlEditorService]
})

  export class ComposeComponent implements OnInit {
    
    public tools: object = {
      items: ['Undo', 'Redo', '|',
          'Bold', 'Italic', 'Underline', 'StrikeThrough', '|',
          'FontName', 'FontSize', 'FontColor', 'BackgroundColor', '|',
          'SubScript', 'SuperScript', '|',
          'LowerCase', 'UpperCase', '|',
          'Formats', 'Alignments', '|', 'OrderedList', 'UnorderedList', '|',
          'Indent', 'Outdent', '|', 'CreateLink',
          'Image', '|', 'ClearFormat', 'Print', 'SourceCode', '|', 'FullScreen']
  };
x:String="";

  to!: String;
  public iframe: object = { enable: true };
  public height: number = 500;
  faPaperclip=faPaperclip
  toText:string = ""
  subjectText:string = ""
  conText:string = ""
  importance :number=0;
  // Text:string = ""
  back(): void {
    this.location.back()
  }
  constructor(private location: Location,  private shared:SharedService,private route:ActivatedRoute, private http:HttpClient,private sanitizer: DomSanitizer) { }

  ngOnInit(): void {
    let mail =this.route.snapshot.paramMap.get('mail')!;
    if (mail!="none")
      this.to=mail
  }
  sendmail(email: mailing):Observable<HttpEvent<any>>
  {
    console.log(email)
    const fd = new FormData()
    fd.append('file',this.attachedFile[0])
    console.log(fd)
    return this.http.post<any>("http://localhost:8080/controller/sendEmail",email)
 
  }
  temp = new Date()
  cMail !: mailing;
  // cMail = 
  submit(){
    console.log(this.to)
    console.log(this.subjectText)
    console.log(this.conText)
    let temp = new Date()
    this.cMail = new mailing(this.shared.getUser(), this.to.split(','), this.subjectText, this.conText, temp.toDateString(), this.importance ,this.attachedFileName); 
    let res !: any
    let resp !: any
    this.sendmail(this.cMail).subscribe(temp =>
    {
       res = temp
       console.log(res)
       if (res=="done"){
        const fd = new FormData()
        for (let file of this.attachedFile)
        fd.append('file',file)
        this.http.post<any>("http://localhost:8080/controller/sendfile",fd).subscribe(tem=>{
          console.log(tem)
        })
  
       }
       

    })
   
  
  }
  attachedFile:File[]=[]
  attachedFileName:String[]=[]
  attachedFileUrl:any[]=[]
  c=0
  x1:any
  select(event: any){
    this.attachedFile.push(<File>event.target.files[0] )
    this.attachedFileName.push(event.target.files[0].name)
    this.x1=URL.createObjectURL(<File>event.target.files[0])
    this.c++;
    this.x1= <string>this.sanitizer.bypassSecurityTrustUrl(this.x1)
    this.attachedFileUrl.push(this.x1)
  }
  remove(i:number){
    this.attachedFile.splice(i,1)
    this.attachedFileName.splice(i,1)
    this.attachedFileUrl.splice(i,1)
    this.x1=""
  }

file64:string=""
  convert(){
  
    let reader=new FileReader();
  reader.readAsDataURL(this.attachedFile[0] as Blob)
  reader.onload=() => {
    this.file64=(reader.result as string)
  }

}

}



class mailing
{
  from!: string;
  to!: string[];
  subject!: string;
  mailContent!:string;
  time!: string;
  importance!:number;
  file!:String[];
  constructor(a:string, b: string[], c: string, d: string, e: string,f: number,g:String[])
  {
    this.from = a
    this.to = b
    this.subject = c
    this.mailContent = d
    this.time = e
    this.importance = f
    this.file= g
  }
  
}