using StudentsAuthorize.BLL;
using StudentsAuthorize.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace StudentsAuthorize.Controllers
{
    [Authorize]
    public class StudentsController : ApiController
    {
        // GET api/values
        public HttpResponseMessage Get()
        {
            StudentsBLL studentsBLL = new StudentsBLL();
            IEnumerable<Student> students = studentsBLL.GetStudents();
            if (students.Count() > 0)
            {
                Students _students = new Students();
                _students.students = students.ToArray();
                return Request.CreateResponse(HttpStatusCode.OK, _students);
            }
            else
            {
                return Request.CreateErrorResponse(HttpStatusCode.NotFound, "No students found");
            }
        }

        [HttpGet]
        [Route("api/IsAdmin")]
        public HttpResponseMessage IsAdmin([FromUri] String username)
        {
            StudentsBLL studentsBLL = new StudentsBLL();
            return studentsBLL.IsAdmin(Request, username);
        }

        [HttpGet]
        //[Route("api/students/GetWithAge/{criteria}/{age}")]
        [Route("api/students/GetWithAge")]
        public HttpResponseMessage GetStudentsWithAge(int age, [FromUri] string criteria)
        {
            StudentsBLL studentsBLL = new StudentsBLL();
            IEnumerable<Student> students = studentsBLL.GetStudentsWithAge(age, criteria);
            if (students.Count() > 0)
            {
                return Request.CreateResponse(HttpStatusCode.OK, students);
            }
            else
            {
                string errMsg = "No students found with age ";
                switch (criteria)
                {
                    case "eq":
                        errMsg += "equals to ";
                        break;
                    case "gt":
                        errMsg += "greater than ";
                        break;
                    case "lt":
                        errMsg += "less than ";
                        break;
                }
                return Request.CreateErrorResponse(HttpStatusCode.NotFound, errMsg + age);
            }
        }

        [HttpGet]
        //[Route("api/students/GetWithGrade/{grade}")]
        [Route("api/students/GetWithGrade")]
        public HttpResponseMessage GetStudentsWithGrade(int grade)
        {
            StudentsBLL studentsBLL = new StudentsBLL();
            IEnumerable<Student> students = studentsBLL.GetStudentsWithGrade(grade);
            if (students.Count() > 0)
            {
                return Request.CreateResponse(HttpStatusCode.OK, students);
            }
            else
            {
                return Request.CreateErrorResponse(HttpStatusCode.NotFound, "No students found in grade " + grade);
            }
        }

        // GET api/values/5
        public HttpResponseMessage Get(int id)
        {
            StudentsBLL studentsBLL = new StudentsBLL();
            Student student = studentsBLL.GetStudent(id);

            if (student != null)
            {
                return Request.CreateResponse(HttpStatusCode.OK, student);
            }
            else
            {
                return Request.CreateErrorResponse(HttpStatusCode.NotFound, "No student found with id " + id);
            }
        }

        // POST api/values
        public HttpResponseMessage Post([FromBody]Student student)
        {
            StudentsBLL studentsBLL = new StudentsBLL();
            return studentsBLL.CreateStudent(Request, student);
        }

        // PUT api/values/5
        public HttpResponseMessage Put(int id, [FromBody]Student student)
        {
            StudentsBLL studentsBLL = new StudentsBLL();
            return studentsBLL.UpdateStudent(Request, id, student);
        }

        // DELETE api/values/5
        public HttpResponseMessage Delete(int id)
        {
            StudentsBLL studentsBLL = new StudentsBLL();
            return studentsBLL.DeleteStudent(Request, id);
        }
    }
}
